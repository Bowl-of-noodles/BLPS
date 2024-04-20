package com.javadevjournal.service;

import com.javadevjournal.dto.AdDTO;
import com.javadevjournal.dto.MessageDTO;
import com.javadevjournal.dto.RankDTO;
import com.javadevjournal.exceptions.NotFoundException;
import com.javadevjournal.jpa.entity.Ad;
import com.javadevjournal.jpa.enums.AdStatus;
import com.javadevjournal.jpa.enums.StatusName;
import com.javadevjournal.jpa.repository.AdsRepository;
import com.javadevjournal.messaging.AdMessage;
import com.javadevjournal.messaging.myConverter;
import com.javadevjournal.security.MyResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service("adsService")
public class AdsServiceImpl implements AdsService {

    private final AdsRepository adsRepository;
    private final JmsTemplate jmsTemplate;
    private final myConverter myConverter;
    @Value("${second.queue.name}")
    private String secondQueue;
    @Value("${last.time.check.update}") //86400 seconds = 1 day = 24 hours
    private Integer afterLastUpdate;
    private final static Logger LOGGER = LoggerFactory.getLogger(AdsServiceImpl.class);



    @Override
    @Transactional
    public List<Ad> findAllAds() {
        return (List<Ad>) adsRepository.findAll();
    }
    @Override
    @Transactional
    public List<Ad> findAdsByFilter(Long minPrice, Long maxPrice, Double weight, String category) {
        return adsRepository.findAllByPriceBetweenAndWeightAndCategory(minPrice, maxPrice, weight, category);
    }

    @Override
    @Transactional
    public List<Ad> findMyAds(Long id) {
        return adsRepository.findAllByOwner(id);
    }


    @Override
    @Transactional
    public void deleteAllByOwner(Long id) {
        adsRepository.deleteAllByOwner(id);
    }

    @Override
    public Ad createAd(AdDTO adDTO, Long id) {
        Ad ad = new Ad();
        ad.setOwner(id);
        ad.setPrice(adDTO.getPrice());
        ad.setWeight(adDTO.getWeight());
        ad.setPhone(adDTO.getPhone());
        ad.setCategory(adDTO.getCategory());
        ad.setDescription(adDTO.getDescription());
        ad.setStatus(AdStatus.INMODERATION);
        ad.setLastTimeUpdateSend(LocalDateTime.now());
        adsRepository.save(ad);

        AdMessage adCheck = new AdMessage();
        adCheck.setAdId(ad.getId());
        adCheck.setDescription(ad.getDescription());
        String message = myConverter.convertAdMessage(adCheck);
        jmsTemplate.convertAndSend(secondQueue, message);

        return ad;
    }


    @JmsListener(destination = "${core.queue.name}")
    public void recieveMessage(Message message) throws JMSException {
        TextMessage msg = (TextMessage) message;
        LOGGER.info("following message is received: " + msg.getText());

        JSONObject jo = new JSONObject(msg.getText());
        String checkResult = jo.getString("check_result");
        Long id = jo.getLong("ad_id");

        Optional<Ad> adOpt = adsRepository.findById(id);
        Ad ad = adOpt.get();
        if(checkResult.equals("acceptable")){
            ad.setStatus(AdStatus.APPROVED);
        }
        else{
            ad.setStatus(AdStatus.BANNED);
        }
        adsRepository.save(ad);
    }


    @Override
    public MessageDTO changeAd(AdDTO adDTO, Long id){
        MessageDTO messageDTO = new MessageDTO();
        Optional<Ad> adOpt = adsRepository.findById(id);
        Ad ad = adOpt.get();

        ad.setPrice(adDTO.getPrice());
        ad.setWeight(adDTO.getWeight());
        ad.setPhone(adDTO.getPhone());
        ad.setCategory(adDTO.getCategory());
        ad.setDescription(adDTO.getDescription());

        adsRepository.save(ad);

        messageDTO.setMessage("ОбЪявление успешно изменено");
        return messageDTO;
    }

    @Override
    public void sendAd(Long id){
        Optional<Ad> adOpt = adsRepository.findById(id);
        Ad ad = adOpt.get();

        AdMessage adCheck = new AdMessage();
        adCheck.setAdId(ad.getId());
        adCheck.setDescription(ad.getDescription());
        String message = myConverter.convertAdMessage(adCheck);
        jmsTemplate.convertAndSend(secondQueue, message);

    }

    @Override
    public void autoSendCheck(){
        /*Optional<Ad> adOpt = adsRepository.findFirstByStatus(AdStatus.BANNED);
        Ad ad = adOpt.get();
        AdMessage adCheck = new AdMessage();
        adCheck.setAdId(ad.getId());
        adCheck.setDescription(ad.getDescription());
        String message = myConverter.convertAdMessage(adCheck);
        jmsTemplate.convertAndSend(secondQueue, message);*/
        adsRepository.findAllByStatus(AdStatus.BANNED).parallelStream()
                .filter(a -> a.getLastTimeUpdateSend().isBefore(LocalDateTime.now().minusSeconds(afterLastUpdate)))
                .forEach(ad -> {
                    AdMessage adCheck = new AdMessage();
                    adCheck.setAdId(ad.getId());
                    adCheck.setDescription(ad.getDescription());
                    String message = myConverter.convertAdMessage(adCheck);
                    jmsTemplate.convertAndSend(secondQueue, message);
                    adsRepository.findById(ad.getId()).get().setLastTimeUpdateSend(LocalDateTime.now());
                    adsRepository.save(ad);
                });

    }



    @Override
    @Transactional
    public Ad getById(Long id) {
        Optional<Ad> ad = adsRepository.findById(id);
        return ad.orElseThrow(() -> new MyResourceNotFoundException("Оюъявление не найден"));
    }

    @Override
    @Transactional
    public void save(Ad ad) {
        adsRepository.save(ad);
    }


    @Override
    @Transactional
    public MessageDTO rank(Long id, RankDTO rankDTO) {
        Optional<Ad> adOpt= adsRepository.findById(id);

        if (adOpt.isEmpty()) {
            throw new NotFoundException("Нет такого объявления");
        }

        var ad = adOpt.get();
        var mark = rankDTO.getRank();
        return rank(ad, mark);
    }

    private MessageDTO rank(Ad ad, Double mark) {
        if ((ad.getRank() != null) && (ad.getRank() > 0)) {
            double rank = ad.getRank();
            rank = (rank + mark) / 2;
            ad.setRank(rank);
        } else {
            ad.setRank(mark);
        }
        MessageDTO message = new MessageDTO();
        message.setMessage("Объявление успешно оценено");
        return message;
    }


}

package com.javadevjournal.security;

import com.javadevjournal.dto.TokenDTO;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class MyXMLWriter {
    private DocumentBuilderFactory dbf;
    private DocumentBuilder db ;
    private Document doc;
    private Element root;
    private List<TokenDTO> tokens = new ArrayList<>();


    private String path = "D:/blse/tokens.xml";


    /*public void generateXMLFile() throws ParserConfigurationException {
        dbf = DocumentBuilderFactory.newInstance();
        db  = dbf.newDocumentBuilder();
        doc = db.newDocument();
        root = doc.createElement("UserTokens");
        doc.appendChild(root);
    }


    public void addToDoc(String name, String inputToken) throws ParserConfigurationException {
        if(doc == null){
            generateXMLFile();
            System.out.println(doc);
            System.out.println("test");
        }
        Element users  = doc.createElement("User");
        Element user = doc.createElement("user");
        user.setTextContent(name);
        Element token = doc.createElement("token");
        token.setTextContent(inputToken);
        users.appendChild(user);
        users.appendChild(token);
        root.appendChild(users);

    }

    public void writeDocument(String userName)
    {
        Transformer trf = null;
        DOMSource src = null;
        FileOutputStream fos = null;
        try {
            trf = TransformerFactory.newInstance().newTransformer();
            src = new DOMSource(doc);
            fos = new FileOutputStream(path);

            StreamResult result = new StreamResult(fos);
            trf.transform(src, result);
        } catch (TransformerException e) {
            e.printStackTrace(System.out);
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }*/
    public void addToken(String username, String token){
        tokens.add(new TokenDTO(username, token));
    }
    public void saveTokensToFile() {
        try {
            File file = new File(path);
            JAXBContext context = JAXBContext.newInstance(UserToken.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Обёртываем наши данные об адресатах.
            UserToken wrapper = new UserToken();
            wrapper.setPersons(tokens);

            // Маршаллируем и сохраняем XML в файл.
            m.marshal(wrapper, file);

        } catch (Exception e) { // catches ANY exception
            e.printStackTrace();
        }
    }
}

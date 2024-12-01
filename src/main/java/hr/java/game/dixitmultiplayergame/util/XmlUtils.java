package hr.java.game.dixitmultiplayergame.util;

import hr.java.game.dixitmultiplayergame.model.GameMove;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class XmlUtils {
    private static final String GAME_MOVES_XML_FILE_NAME = "xml/gameMoves.xml";
    public static void saveGameMovesToXml(List<GameMove> gameMoves) {

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("GameMoves");
            doc.appendChild(rootElement);

            for(GameMove gameMove : gameMoves) {
                Element gameMoveElement = doc.createElement("GameMove");

                Element symbolElement = doc.createElement("CardFilename");
                symbolElement.setTextContent(gameMove.getCardName());
                gameMoveElement.appendChild(symbolElement);

                Element localDateTimeElement = doc.createElement("LocalDateTime");
                localDateTimeElement.setTextContent(gameMove.getFormattedLocalDateTime());
                gameMoveElement.appendChild(localDateTimeElement);

                rootElement.appendChild(gameMoveElement);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new FileWriter(GAME_MOVES_XML_FILE_NAME));
            transformer.transform(source, result);

        } catch (ParserConfigurationException | IOException | TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<GameMove> readGameMovesFromXml() {

        List<GameMove> gameMoves = new ArrayList<>();

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document dom = db.parse(GAME_MOVES_XML_FILE_NAME);
            Element gameMoveElement = dom.getDocumentElement();
            NodeList nl = gameMoveElement.getChildNodes();

            int length = nl.getLength();
            for (int i = 0; i < length; i++) {
                if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) nl.item(i);
                    if (el.getNodeName().contains("GameMove")) {
                        String cardFilename = String.valueOf(el.getElementsByTagName("CardFilename").item(0).getTextContent());
                        LocalDateTime localDateTime = LocalDateTime.parse(el.getElementsByTagName("LocalDateTime").item(0).getTextContent(), GameMove.formatter);

                        GameMove gameMove = new GameMove();
                        gameMove.setCardName(cardFilename);
                        gameMove.setLocalDateTime(localDateTime);

                        gameMoves.add(gameMove);
                    }
                }
            }
        }
        catch(ParserConfigurationException | IOException | SAXException ex) {
            throw new RuntimeException(ex);
        }

        return gameMoves;
    }

}

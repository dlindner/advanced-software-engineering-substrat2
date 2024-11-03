package io.nyando.factorix.config.adapters;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.ArrayList;
import java.util.List;

public class XMLConfigAdapter extends ConfigFileAdapter {

    public XMLConfigAdapter(String workplaceConfigFilePath,
                            String productConfigFilePath) throws FileReadException {
        super(workplaceConfigFilePath, productConfigFilePath);
    }

    protected void readWorkplaceFile() throws FileReadException {
        SAXReader reader = new SAXReader();
        try {
            Document doc = reader.read(this.workplaceConfigFile);
            Element workplaceList = doc.getRootElement();

            for (Element workplace : workplaceList.elements()) {
                String workplaceID = workplace.attributeValue("id");
                String processType = workplace.attributeValue("process");
                this.workplaceProcesses.put(workplaceID, processType);

                for (Element procTime : workplace.elements()) {
                    String productType = procTime.attributeValue("product");
                    int processTime = Integer.parseInt(procTime.getStringValue());
                    this.workplaceProductTimes.put(workplaceID, productType, processTime);
                }
            }
        } catch (DocumentException ex) {
            throw new FileReadException();
        }
    }

    protected void readProductFile() throws FileReadException {
        SAXReader reader = new SAXReader();

        try {
            Document doc = reader.read(this.productConfigFile);
            Element productList = doc.getRootElement();

            for (Element product : productList.elements()) {
                String productType = product.attributeValue("id");

                List<String> processList = new ArrayList<>();
                for (Element process : product.elements()) {
                    processList.add((String) process.getData());
                }

                this.productProcesses.put(productType, processList);
            }
        } catch (DocumentException ex) {
            throw new FileReadException();
        }
    }

}

package com.bitconex.ordermanagement.orderingprocess.order;

import com.bitconex.ordermanagement.orderingprocess.orderitem.OrderItemDTO;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class OrderExportService {

    @Autowired
    OrderService orderService;

    private static final Logger logger = LoggerFactory.getLogger(OrderExportService.class);

    public void exportOrdersToCsv(String directoryPath, String fileName) {
        try {
            if (!fileName.endsWith(".csv")) {
                fileName += ".csv";
            }
            String filePath = directoryPath + File.separator + fileName;

            // Provjera da li je direktorijum pristupačan i postoji
            Path path = Paths.get(filePath).getParent();
            if (Files.notExists(path) || !Files.isWritable(path)) {
                logger.error("Unable to access or write to directory: " + path);
                return;
            }

            List<OrderDTO> orders = orderService.getOrdersDTO();
            try (Writer writer = new FileWriter(filePath);
                 CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                         .withHeader("Order ID", "Product Name", "Product Price"))) {

                String currentOrderId = null;
                for (OrderDTO order : orders) {
                    if (!order.getId().equals(currentOrderId)) {
                        // Dodajte prazan red između narudžbi
                        csvPrinter.printRecord("");

                        // istakne koji order je u pitanju
                        String boldOrderId = "Order ID: " + order.getId();
                        csvPrinter.printRecord(boldOrderId);
                        String customer = "Customer: " + order.getUser().getUsername();
                        csvPrinter.printRecord(customer);

                        currentOrderId = String.valueOf(order.getId());
                    }

                    for (OrderItemDTO orderItem : order.getOrderItemDTOList()) {
                        csvPrinter.printRecord("", orderItem.getProduct().getName(), orderItem.getProduct().getPrice());
                    }

                    csvPrinter.printRecord("Total Price:", "", order.getTotalPrice());
                }
                csvPrinter.flush();
            }
            logger.info("Orders have been successfully exported to a CSV file: " + filePath);
        } catch (IOException e) {
            logger.error("Error when exporting orders to CSV file.", e);
        }
    }
}

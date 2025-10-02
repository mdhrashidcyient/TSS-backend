package com.example.after_market_db_tool.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.after_market_db_tool.entity.AfterMarketVendors;
import com.example.after_market_db_tool.repository.AfterMarketVendorsRepository;

@Service
public class LoadDataToPostGres {
	
	@Autowired
	AfterMarketVendorsRepository repo;
	
	public void uploadDataToPostGres(MultipartFile file) {
		
		//String filePath = "C:/Users/mr69509/OneDrive - Cyient Ltd/Documents/amv_data.txt"; 
		BufferedReader reader = null; 
		
		try {      
            

            // Wrap the FileReader with a BufferedReader for efficient reading
            reader = new BufferedReader(new InputStreamReader(file.getInputStream(),StandardCharsets.UTF_8));
            


            String line;
            List<AfterMarketVendors> amvList = new ArrayList();
            // Read lines until the end of the file (readLine() returns null)
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // Print each line to the console
                String [] lineItems = line.split("~");   
                //System.out.println(lineItems.length); //
               
                AfterMarketVendors amv = new AfterMarketVendors();
                amv.setSupplier(lineItems[0].trim());
                amv.setAnalystSupporting(lineItems[1].trim());
                amv.setAddress(lineItems[2].trim());
                amv.setVendorCode(lineItems[3].trim());
                amv.setCommutingVendor(lineItems[4].trim());
                amv.setNiche(lineItems[5].trim());
                amv.setEmail(lineItems[6].trim());
                amv.setContactName(lineItems[7].trim());
                amv.setPhone(lineItems[8].trim());
                amv.setWipEmail(lineItems[9].trim());
                amv.setComments(lineItems[10].trim());
                amv.setActive(lineItems[11].trim());              
                //amvList.add(amv);
                if(repo != null) {
                 repo.save(amv);
                }

                               
            }
            //System.out.println(amvList.size());
           //repo.saveAll(amvList);
        } catch (IOException e) {
            // Handle any IOException that might occur during file operations
            System.err.println("An error occurred while reading the file: " + e.getMessage());
            e.printStackTrace(); // Print the stack trace for debugging
        }
		finally {
            // Ensure the BufferedReader is closed to release system resources
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.err.println("Error closing the BufferedReader: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
	}

}

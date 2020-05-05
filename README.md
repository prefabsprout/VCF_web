# VCF Web

## Deploying of app

To deploy an app you need to create MySQL database in your local host.  
Root user need sudo priveleges to login to MySQL, so let create a newuser and grant him all priveleges to access all databases:  
``` CREATE USER 'newuser'@'localhost' IDENTIFIED BY 'password';```  
``` GRANT ALL PRIVILEGES ON *.* TO 'newuser'@'localhost'; ```  
Next you need to create database:  
``` CREATE DATABASE vcf; ```  
Next you need to build and run ```db_factory.kt``` to create table in our database, which will contain VCF data.  
After that move ```42.tsv``` into ```/var/lib/mysql-files/42.tsv``` and run in your MySQL command line:  
```USE vcf;```  
```LOAD DATA INFILE '/var/lib/mysql-files/42.tsv' INTO TABLE VCF_data (contig, left_boundary, right_boundary, nucleotide, rs);```  
```CREATE UNIQUE INDEX id ON VCF_data (id);```  
Finally build and run ```Application.kt``` to deploy app.

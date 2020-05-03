# VCF Web

## Deploying of app

To deploy an app you need to create MySQL database in your local host.  
Root user need sudo priveleges to login to MySQL, so let create a newuser and grant him all priveleges to access all databases:  
``` CREATE USER 'newuser'@'localhost' IDENTIFIED BY 'password';```  
``` GRANT ALL PRIVILEGES ON *.* TO 'newuser'@'localhost'; ```  
Next you need to create database:  
``` CREATE DATABASE vcf; ```  
Next you need to build and run ```db_factory.kt``` to create table in our database, which will contain VCF data.  
Finally build and run ```Application.kt``` to deploy app.

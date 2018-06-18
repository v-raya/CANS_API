#INSTRUMENT PARSER

#####A tool to convert CSV file with domains/items descriptions into liquibase scripts with i18n records and json file with assessment structure 

- **`./csv/cans_domains.csv`** file is a source for domains
- **`./csv/cans_items.csv`** file is a source for items
- **`./csv/structure-converter.csv`** file is a source for basic assessment structure

To run the app write in terminal:
`npm start`

Generated database changelog and assesment.json will be created in the **`./output`** folder

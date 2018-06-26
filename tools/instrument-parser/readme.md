#INSTRUMENT PARSER

#####A tool to convert CSV file with domains/items descriptions into liquibase scripts with i18n records and json file with assessment structure 

- **`./csv/cans_domains.csv`** file is a source for domains
- **`./csv/cans_items.csv`** file is a source for items
- **`./csv/cans_to_domains_to_items.csv`** file is a source for basic assessment structure

To run the app write in terminal:
`npm i && npm start`

Generated database changelogs for the new assessment and it's I18n will be created in the **`./output`** folder. To use them in project copy-paste them into **`src/main/resources/liquibase/ca`** folder and _include_ in **`src/main/resources/liquibase/cans_database_demo_master.xml`**

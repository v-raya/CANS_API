const chalk = require('chalk');
const figlet = require('figlet');
const Papa = require('papaparse');
const fs = require('fs');
const path = require('path');
const domainConverter = require('./js/domain-to-object-converter');
const itemConverter = require('./js/item-to-object-converter');
const liquibaseConverter = require('./js/object-to-liquibase');

console.log(chalk.yellow(
    figlet.textSync('CSV - i18n', {horizontalLayout: 'full'})
));

const csvToArray = csvFilePath => {
  const domainsCsvPath = path.join(__dirname, csvFilePath);
  const domainsCsv = fs.readFileSync(domainsCsvPath, 'utf8');
  return Papa.parse(domainsCsv, {
    header: true,
    complete: function(results) {
      // console.log("Finished:", results.data);
      return results;
    }
  });
};

const rawDomains = csvToArray('csv/cans_domains.csv');
const domainObjectArray = domainConverter.toDomainObjectArray(rawDomains.data);

const rawItems = csvToArray('csv/cans_items.csv');
const itemObjectArray = itemConverter.toItemsObjectArray(rawItems.data);

const mergedArray = domainObjectArray.concat(itemObjectArray);
const liquibaseChangeset = liquibaseConverter.toLiquibaseChangeset(mergedArray);
console.log(liquibaseChangeset);

const xmlPath = "output/changelog.xml";
fs.writeFile(xmlPath, liquibaseChangeset, function(err) {
  if(err) {
    return console.log(err);
  }

  console.log(`The '${xmlPath}' file was saved!`);
});


const chalk = require('chalk');
const figlet = require('figlet');
const Papa = require('papaparse');
const fs = require('fs');
const path = require('path');
const domainConverter = require('./js/domain-to-object-converter');
const itemConverter = require('./js/item-to-object-converter');
const linkConverter = require('./js/structure-converter');
const liquibaseConverter = require('./js/object-to-liquibase');

console.log(chalk.yellow(
    figlet.textSync('CSV > assessment + i18n', {horizontalLayout: 'full'})
));

const csvToArray = csvFilePath => {
  const domainsCsvPath = path.join(__dirname, csvFilePath);
  const domainsCsv = fs.readFileSync(domainsCsvPath, 'utf8');
  return Papa.parse(domainsCsv, {
    header: true,
    trimHeaders: true,
    complete: function(results) {
      // console.log("Finished:", results.data);
      return results;
    }
  });
};

const saveFile = (filePath, fileBody) => {
  fs.writeFile(filePath, fileBody, function(err) {
    if(err) {
      return console.log(err);
    }
    console.log(`The '${filePath}' file was saved!`);
  });
};

// generate i18n liquibase scripts
const rawDomains = csvToArray('csv/cans_domains.csv');
const rawItems = csvToArray('csv/cans_items.csv');
const rawLink = csvToArray('csv/cans_to_domains_to_items.csv');

const domainObjectArray = domainConverter.toDomainObjectArray(rawDomains.data);
const itemObjectArray = itemConverter.toItemsObjectArray(rawItems.data);

const mergedArray = domainObjectArray.concat(itemObjectArray);
const liquibaseChangeset = liquibaseConverter.toLiquibaseChangeset(mergedArray);
saveFile("output/changelog.xml", liquibaseChangeset);
console.log(liquibaseChangeset);

// generate assessment json
const assessmentDraft = linkConverter.toAssessmentDraft(rawLink.data);
const assessment = linkConverter.enrichAssessmentWithDomainsAndItems(
    assessmentDraft, rawDomains.data, rawItems.data);
saveFile("output/assessment.json", JSON.stringify(assessment));
console.log(JSON.stringify(assessment));



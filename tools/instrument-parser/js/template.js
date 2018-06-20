exports.changesetTemplate =
    '<?xml version="1.0" encoding="UTF-8" standalone="no"?>\n'
    + '<databaseChangeLog \n'
    + '  logicalFilePath="ca/add_ca_instrument_i18n.xml"\n'
    + '  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"\n'
    + '  xmlns="http://www.liquibase.org/xml/ns/dbchangelog"\n'
    + '  xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog\n'
    + '  http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.3.xsd">\n'
    + '\n'
    + '  <changeSet id="instrument-ca-i18n-insert" author="denys.davydov">\n'
    + '  %INSERTS_PLACE_HOLDER%'
    + '    <rollback>\n'
    + '      <delete tableName="i_18_n">\n'
    + '        <where>lang = \'en\' and k like \'instrument.1%\'</where>\n'
    + '      </delete>\n'
    + '    </rollback>\n'
    + '  </changeSet>\n'
    + '</databaseChangeLog>\n';

exports.INSERTS_PLACE_HOLDER = '%INSERTS_PLACE_HOLDER%';

exports.insertRecordTemplate =
    '    <insert tableName="i_18_n">\n'
    + '      <column name="lang" value="en"/>\n'
    + '      <column name="k" value="%KEY_PLACEHOLDER%"/>\n'
    + '      <column name="v" value="%VALUE_PLACEHOLDER%"/>\n'
    + '    </insert>\n';

exports.KEY_PLACEHOLDER = '%KEY_PLACEHOLDER%';
exports.VALUE_PLACEHOLDER = '%VALUE_PLACEHOLDER%';

exports.assessmentTemplate = {
  "under_six": false,
  "domains": []
};

exports.domainTemplate = {
  "id": null,
  "class": "domain",
  "code": null,
  "under_six": null,
  "above_six": null,
  "items": []
};

exports.itemTemplate = {
  "id": null,
  "under_six_id": null,
  "above_six_id": null,
  "code": null,
  "required": true,
  "confidential": false,
  "rating_type": null,
  "rating": -1
};
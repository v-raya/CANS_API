exports.changesetTemplate =
    '<?xml version="1.0" encoding="UTF-8" standalone="no"?>\n'
    + '<databaseChangeLog xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"\n'
    + '  xmlns="http://www.liquibase.org/xml/ns/dbchangelog"\n'
    + '  xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog\n'
    + '  http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.3.xsd">\n'
    + '\n'
    + '  <changeSet id="RENAME" author="PLACE_YOU_NAME_HERE">\n'
    + '  %INSERTS_PLACE_HOLDER%'
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
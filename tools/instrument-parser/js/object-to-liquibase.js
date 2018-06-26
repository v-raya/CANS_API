const template = require('./template');

const HARDCODED_INSTRUMENT_ID = 1;

exports.toLiquibaseChangeset = parsedArray => {
  const insertRecords = [];

  let i;
  const length = parsedArray.length;
  for (i = 0; i < length; i++) {
    const parsedElement = parsedArray[i];
    const code = parsedElement.code;
    const i18ns = parsedElement.i18ns;
    let j;
    const i18nLength = i18ns.length;
    for (j = 0; j < i18nLength; j++) {
      const i18n = i18ns[j];
      const key = `instrument.${HARDCODED_INSTRUMENT_ID}.${code}.${i18n.key}`;
      const value = i18n.value.trim().replace(/\r?\n|\r|\u2028|\u2029/g, '')
          .replace(/\s\s+/g, ' ')
          .replace(/&/g, '&amp;')
          .replace(/"/g, '&quot;')
          .replace(/</g, '&lt;')
          .replace(/>/g, '&gt;');
      const insertRecord = template.insertI18nRecordTemplate.replace(
          template.KEY_PLACEHOLDER, key).replace(template.VALUE_PLACEHOLDER,
          value);
      insertRecords.push(insertRecord);
    }
  }
  return template.changesetI18nTemplate.replace(template.INSERTS_PLACE_HOLDER,
      insertRecords.join(''));
};
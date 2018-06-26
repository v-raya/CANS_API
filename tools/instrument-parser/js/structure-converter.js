const template = require('./template');
const _cloneDeep = require('lodash/fp/cloneDeep');

exports.toAssessmentDraft = parsedArray => {
  const results = _cloneDeep(template.assessmentTemplate);

  let domain = _cloneDeep(template.domainTemplate);
  domain.id = "1";

  let i;
  const length = parsedArray.length;
  for (i = 0; i < length; i++) {
    const parsedElement = parsedArray[i];
    const domainId = parsedElement['Domain_ID'];
    if (!domainId) {
      continue;
    }

    if (domain.id !== domainId) {
      results.domains.push(_cloneDeep(domain));
      domain = _cloneDeep(template.domainTemplate);
      domain.id = domainId;
    }

    const item = _cloneDeep(template.itemTemplate);
    item.id = parsedElement['Item_ID'];
    item.under_six_id = parsedElement['CANS_0_5_ID'];
    item.above_six_id = parsedElement['CANS_6_21_ID'];
    domain.items.push(_cloneDeep(item));
  }
  results.domains.push(_cloneDeep(domain));
  return results;
};

exports.enrichAssessmentWithDomainsAndItems = (assessment, rawDomains, rawItems) => {
  assessment.domains.forEach(domain => {
    const rawDomain = getRawDomainById(rawDomains, domain.id);
    domain.code = rawDomain['Domain_Abbr'];
    domain.under_six = rawDomain['0-5'] === "1";
    domain.above_six = rawDomain['6-21'] === "1";

    domain.items.forEach(item => {
      const rawItem = getRawItemById(rawItems, item.id);
      delete item.id;
      if (!rawItem) return;
      item.code = rawItem['Item_Abbr'];
      item.confidential_by_default = rawItem['Confidential_by_Default'] === '1';
      item.confidential = item.confidential_by_default;
      item.rating_type = rawItem['Rate_Yes'].trim() ? 'BOOLEAN' : 'REGULAR';
      item.has_na_option = !!rawItem['Rate_NA'].trim();
    });
  });
  return assessment;
};

const getRawDomainById = (rawDomains, id) => {
  let i;
  const length = rawDomains.length;
  for (i = 0; i < length; i++) {
    const rawDomain = rawDomains[i];
    if (rawDomain['Domain_ID'] === id) {
      return rawDomain;
    }
  }
};

const getRawItemById = (rawItems, id) => {
  let i;
  const length = rawItems.length;
  for (i = 0; i < length; i++) {
    const rawItem = rawItems[i];
    if (rawItem['Item_ID'] === id) {
      return rawItem;
    }
  }
};

exports.toItemsObjectArray = parsedArray => {
  const results = [];

  let i;
  const length = parsedArray.length;
  for (i = 0; i < length; i++) {
    const parsedElement = parsedArray[i];
    const code = parsedElement['Item_Abbr'];
    if (!code) {
      console.log(`Skipped parsed item = ${JSON.stringify(parsedElement)} because Item_Abbr is undefined`);
      continue;
    }
    const id = parsedElement['Item_ID'];
    if (!id) {
      console.log(`Skipped parsed item = ${JSON.stringify(parsedElement)} because Item_ID is undefined`);
      continue;
    }

    const newElement = {
      id,
      code,
      i18ns: []
    };

    mapToNewElement(parsedElement, 'Item_Name', newElement, '_title_');
    mapToNewElement(parsedElement, 'Description', newElement, '_description_');
    mapToNewElement(parsedElement, 'Rate_0', newElement,
        '_rating_.0._description_');
    mapToNewElement(parsedElement, 'Rate_1', newElement,
        '_rating_.1._description_');
    mapToNewElement(parsedElement, 'Rate_2', newElement,
        '_rating_.2._description_');
    mapToNewElement(parsedElement, 'Rate_3', newElement,
        '_rating_.3._description_');
    mapToNewElement(parsedElement, 'Rate_No', newElement,
        '_rating_.0._description_');
    mapToNewElement(parsedElement, 'Rate_Yes', newElement,
        '_rating_.1._description_');
    mapQuestionsToConsider(parsedElement, newElement);
    results.push(newElement);
  }
  return results;
};

const mapToNewElement = (parsedElement, parsedKey, newElement, newKey) => {
  const value = parsedElement[parsedKey];
  if (value) {
    newElement.i18ns.push({
      key: newKey,
      value : value
    });
  }
};

const mapQuestionsToConsider = (parsedElement, newElement) => {
  const value = parsedElement['Questions'];
  if (value) {
    const trimmedValue = value.trim();
    if ((trimmedValue.match(/•/g) || []).length <= 1) {
      newElement.i18ns.push({
        key: '_to_consider_.0',
        value : trimmedValue.replace(/\s\s+/g, ' ')
      });
    } else {
      const splitted = trimmedValue.split('•');
      let i = 0;
      splitted.map(question => {
        const trimmed = question.trim();
        if (trimmed) {
          newElement.i18ns.push({
            key: '_to_consider_.' + i++,
            value : trimmed.replace(/\s\s+/g, ' ')
          });
        }
      })
    }
  }
};

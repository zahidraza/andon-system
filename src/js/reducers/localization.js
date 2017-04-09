import { getLocaleData } from 'grommet/utils/Locale';

getLocaleDataFromJson();

export  function localeData() {
  return JSON.parse(window.localStorage.getItem("localeData")).messages;
}

function getLocaleDataFromJson() {
  let locale = navigator.language;

  // Resource
  let messages;
  try {
    messages = require('../resource/' + locale);
  } catch (e) {
    messages = require('../resource/en-US');
  }
  let localeDate = getLocaleData(messages, locale);

  // Set the locale data in the local storage within browser
  window.localStorage.setItem("localeData", JSON.stringify(localeDate));

}

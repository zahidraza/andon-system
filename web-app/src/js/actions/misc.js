import axios from "axios";

import {MISC_CONSTANTS as m, USER_CONSTANTS as u, NAV_ACTIVATE} from  '../utils/constants';
import {getHeaders} from  '../utils/restUtil';

export function initialize () {
  console.log("initialize()");

  return function (dispatch) {

    axios.all([
      axios.get(window.serviceHost + '/v2/teams', {headers: getHeaders()}),
      axios.get(window.serviceHost + '/v2/problems', {headers: getHeaders()}),
      axios.get(window.serviceHost + '/v2/buyers', {headers: getHeaders()}),
      axios.get(window.serviceHost + '/v1/sections', {headers: getHeaders()}),
      axios.get(window.serviceHost + '/v1/departments', {headers: getHeaders()}),
      axios.get(window.serviceHost + '/v1/problems', {headers: getHeaders()}),
      axios.get(window.serviceHost + '/v1/designations', {headers: getHeaders()}),
      axios.get(window.serviceHost + '/v2/users', {headers: getHeaders()})
    ])
    .then(axios.spread(function (teams, problems, buyers, sections, departments, problms, designations, users) {
      dispatch({type: m.INITIALIZE_TEAM, payload: { teams: teams.data }});
      dispatch({type: m.INITIALIZE_PROBLEM, payload: {problems:  problems.data }});
      dispatch({type: m.INITIALIZE_BUYER, payload: { buyers: buyers.data }});
      dispatch({type: m.INITIALIZE_SECTION, payload: { sections: sections.data }});
      dispatch({type: m.INITIALIZE_DEPARTMENT, payload: {departments:  departments.data }});
      dispatch({type: m.INITIALIZE_PROBLM, payload: { problms: problms.data }});
      dispatch({type: m.INITIALIZE_DESGN, payload: { desgns: designations.data }});
      dispatch({type: u.INITIALIZE_USER, payload: { users: users.data.users }});
      dispatch({type: m.STORE_INITIALIZED});

    }))
    .catch( (err) => {
      console.log(err); 
    });

  };
}

export function navActivate (active) {
  return { type: NAV_ACTIVATE, payload: {active}};
}

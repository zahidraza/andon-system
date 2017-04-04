import axios from "axios";

import {MISC_CONSTANTS as m, USER_CONSTANTS as u} from  '../utils/constants';

export function initialize () {
  console.log("initialize()");

  return function (dispatch) {

    axios.all([
      axios.get(window.serviceHost + '/v2/teams'),
      axios.get(window.serviceHost + '/v2/problems'),
      axios.get(window.serviceHost + '/v2/buyers'),
      axios.get(window.serviceHost + '/v2/users')
    ])
    .then(axios.spread(function (teams, problems,buyers, users) {
      // console.log(teams);
      // console.log(problems);
      // console.log(buyers);
      // console.log(users);
      dispatch({type: m.INITIALIZE_TEAM, payload: { teams: teams.data }});
      dispatch({type: m.INITIALIZE_PROBLEM, payload: {problems:  problems.data }});
      dispatch({type: m.INITIALIZE_BUYER, payload: { buyers: buyers.data }});
      dispatch({type: u.INITIALIZE_USER, payload: { users: users.data }});
      dispatch({type: m.STORE_INITIALIZED});

    }))
    .catch( (err) => {
      console.log(err); 
    });

  };
}

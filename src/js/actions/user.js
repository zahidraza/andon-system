import axios from "axios";

import {USER_CONSTANTS as c} from  '../utils/constants';
import {getHeaders} from  '../utils/restUtil';

export function authenticate (username, password) {
  console.log('authenticate');

  return function (dispatch) {

    const config = {
      method: 'post',
      url: "http://localhost:8001/oauth/token",
      headers: {'Authorization': 'Basic ' + btoa('client-web:super-secret')},
      params: {
        grant_type: 'password',
        username: username,
        password: password
      }
    };

    axios(config)
    .then((response) => {
      if (response.status == 200) {
        dispatch({type: c.USER_AUTH_SUCCESS, payload: {username, data: response.data}});
      }else{
        console.log(response);
      }

    }).catch( (err) => {
      console.log(err);
      dispatch({type: c.USER_AUTH_FAIL});
    });
  };
  
}


export function addUser (user) {
  console.log('addUser');

  return function (dispatch) {
    console.log(user);
    axios.post(window.serviceHost + '/v2/users', JSON.stringify(user), {headers: getHeaders()})
    .then((response) => {
      console.log(response);
      if (response.status == 201) {
        dispatch({type: c.USER_ADD_SUCCESS, payload: {user: response.data}});
      }
    }).catch( (err) => {
      console.log(err);
      dispatch({type: c.USER_ADD_FAIL});
    });
  };
}

export function updateUser (user) {
  console.log('updateUser');
  return function (dispatch) {
    console.log(user);
    axios.put(window.serviceHost + '/v2/users/' + user.id, JSON.stringify(user), {headers: getHeaders()})
    .then((response) => {
      console.log(response);
      if (response.status == 200) {
        dispatch({type: c.USER_EDIT_SUCCESS, payload: {user: response.data}});
      }
    }).catch( (err) => {
      console.log(err);
      dispatch({type: c.USER_EDIT_FAIL});
    });
  };
}

export function removeUser (user) {
  console.log('removeUser');
  return function (dispatch) {
    console.log(user);

    axios.delete(window.serviceHost + '/v2/users/' + user.id, {headers: getHeaders()})
    .then((response) => {
      console.log(response);
      dispatch({type: c.USER_REMOVE_SUCCESS, payload: {user: user}});
    }).catch( (err) => {
      console.log(err);
      dispatch({type: c.USER_REMOVE_FAIL});
    });
  };
}

// export function getUsers () {
//   console.log("getUsers()");

//   return function (dispatch) {
//     dispatch({type:c.USER_FETCH_PROGRESS});

//     axios.get(window.serviceHost + '/users')
//     .then((response) => {
//       if (response.status == 200 && response.data._embedded) {
//         dispatch({type: c.USER_FETCH_SUCCESS, payload: {users: response.data._embedded.userList}});
//       }
//     }).catch( (err) => {
//       console.log(err); 
//       dispatch({type: c.USER_FETCH_FAIL});
//     });
//   };
// }


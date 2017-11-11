import axios from "axios";

import {USER_CONSTANTS as c} from  '../utils/constants';
import {getHeaders} from  '../utils/restUtil';

axios.interceptors.response.use(function (response) {
  // Do something with response data
  return response;
}, function (error) {
  if (error.response.status == 401) {
    delete sessionStorage.session;
  }
  return Promise.reject(error);
});

export function authenticate (username, password) {
  return function (dispatch) {
    dispatch({type: c.USER_AUTH_PROGRESS});
    const config = {
      method: 'post',
      url: window.baseUrl + "/oauth/token",
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
        //console.log(response);
      }

    }).catch( (err) => {
      if (err.response && err.response.status == 400 && err.response.data && err.response.data.error_description == 'User is disabled') {
        alert('Your account has been deactivated.');
      }
      dispatch({type: c.USER_AUTH_FAIL});
    });
  };
  
}


export function searchUser (email) {
  return function (dispatch) {
    dispatch({type: c.USER_BUSY});
    axios.get(window.serviceHost + '/v2/users/search/byEmail?email=' + email, {headers: getHeaders()})
    .then((response) => {
      if (response.status == 200) {
        dispatch({type: c.USER_SEARCH_SUCCESS, payload: {user: response.data}});
      }
    }).catch( (err) => {
      dispatch({type: c.USER_SEARCH_FAIL});
    });
  };
}



export function addUser (user) {
  return function (dispatch) {
    dispatch({type: c.USER_BUSY});
    axios.post(window.serviceHost + '/v2/users', JSON.stringify(user), {headers: getHeaders()})
    .then((response) => {
      if (response.status == 201) {
        dispatch({type: c.USER_ADD_SUCCESS, payload: {user: response.data}});
      }
    }).catch( (err) => {
      if (err.response.status == 400) {
        dispatch({type: c.USER_BAD_REQUEST, payload: {errors: err.response.data}});
      }else if (err.response.status == 409) {
        alert(err.response.data.message);
        dispatch({type: c.USER_ADD_FAIL});
      }else {
        dispatch({type: c.USER_ADD_FAIL});
      }
    });
  };
}

export function updateUser (user, deactivate) {
  return function (dispatch) {
    dispatch({type: c.USER_BUSY});
    axios.put(window.serviceHost + '/v2/users/' + user.id, JSON.stringify(user), {headers: getHeaders()})
    .then((response) => {
      if (response.status == 200) {
        dispatch({type: c.USER_EDIT_SUCCESS, payload: {user: response.data, deactivate}});
      }
    }).catch( (err) => {
      if (err.response.status == 400) {
        dispatch({type: c.USER_BAD_REQUEST, payload: {errors: err.response.data}});
      }else if (err.response.status == 409) {
        alert(err.response.data.message);
        dispatch({type: c.USER_EDIT_FAIL});
      }else {
        dispatch({type: c.USER_EDIT_FAIL});
      }
    });
  };
}

export function changePassword (credential) {
  return function (dispatch) {
    //dispatch({type: c.USER_AUTH_PROGRESS});
    axios.put(window.serviceHost + '/v2/misc/change_password?email=' +  credential.email + "&oldPassword=" + credential.oldPassword + "&newPassword=" + credential.newPassword, null, {headers: getHeaders()})
    .then((response) => {
      if (response.status == 200) {
        if (response.data.status == "SUCCESS") {
          dispatch({type: c.USER_CHANGE_PASSWD, payload: {message: 'Password changed successfully.'}});
        }else{
          dispatch({type: c.USER_CHANGE_PASSWD, payload: {message: 'Incorrect credential. try again!'}});
        }
        //dispatch({type: c.USER_EDIT_SUCCESS, payload: {user: response.data}});
      }
    }).catch( (err) => {
      dispatch({type: c.USER_CHANGE_PASSWD, payload: {message: 'Some error occured'}});
    });
  };
}

export function removeUser (user) {
  return function (dispatch) {
    axios.delete(window.serviceHost + '/v2/users/' + user.id, {headers: getHeaders()})
    .then((response) => {
      dispatch({type: c.USER_REMOVE_SUCCESS, payload: {user: user}});
    }).catch( (err) => {
      if (err.response.status == 409) {
        alert(err.response.data.message);
        dispatch({type: c.USER_REMOVE_FAIL});
      }else {
        dispatch({type: c.USER_REMOVE_FAIL});
      }
    });
  };
}


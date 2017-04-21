import axios from "axios";

import {USER_CONSTANTS as c} from  '../utils/constants';
import {getHeaders} from  '../utils/restUtil';

axios.interceptors.response.use(function (response) {
  // Do something with response data
  console.log(response);
  return response;
}, function (error) {
  if (error.response.status == 401) {
    delete sessionStorage.session;
  }
  return Promise.reject(error);
});

export function authenticate (username, password) {
  console.log('authenticate');

  return function (dispatch) {
    dispatch({type: c.USER_AUTH_PROGRESS});
    const config = {
      method: 'post',
      url: "http://localhost:8001/oauth/token",
      // url: "http://zahidraza.in/andon-system/oauth/token",
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
      if (err.response.status == 400) {
        dispatch({type: c.USER_BAD_REQUEST, payload: {errors: err.response.data}});
      }
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
      if (err.response.status == 400) {
        dispatch({type: c.USER_BAD_REQUEST, payload: {errors: err.response.data}});
      }
    });
  };
}

export function changePassword (credential) {
  console.log('updateUser');
  return function (dispatch) {
    dispatch({type: u.USER_AUTH});
    axios.put(window.serviceHost + '/v2/misc/change_password?userId=' +  credential.userId + "&oldPassword=" + credential.oldPassword + "&newPassword=" + credential.newPassword, null, {headers: getHeaders()})
    .then((response) => {
      console.log(response);
      if (response.status == 200) {
        if (response.data.status == "SUCCESS") {
          dispatch({type: c.USER_CHANGE_PASSWD, payload: {message: 'Password changed successfully.'}});
        }else{
          dispatch({type: c.USER_CHANGE_PASSWD, payload: {message: 'Incorrect credential. try again!'}});
        }
        //dispatch({type: c.USER_EDIT_SUCCESS, payload: {user: response.data}});
      }
    }).catch( (err) => {
      console.log(err);
      dispatch({type: c.USER_CHANGE_PASSWD, payload: {message: 'Some error occured'}});
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


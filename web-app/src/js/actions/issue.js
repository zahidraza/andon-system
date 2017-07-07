import axios from "axios";

import {ISSUE_CONSTANTS as c} from  '../utils/constants';
import {getHeaders} from  '../utils/restUtil';

axios.interceptors.response.use(function (response) {
  return response;
}, function (error) {
  if (error.response.status == 401) {
    delete sessionStorage.session;
  }
  return Promise.reject(error);
});

export function syncIssue (sync) {

  return function (dispatch) {
    dispatch({type: c.ISSUE_BUSY, payload: {busy: true}});
    const issueSync = sync == undefined ? 0 : sync;
    axios.get(window.serviceHost + '/v2/issues?start=' + issueSync, {headers: getHeaders()})
    .then((response) => {
      if (response.status == 200) {
        dispatch({type: c.ISSUE_SYNC_SUCCESS, payload: {issue: response.data}});
      }
    }).catch( (err) => {
      dispatch({type: c.ISSUE_SYNC_FAIL});
    });
  };
}


export function addIssue (issue) {
  return function (dispatch) {
    dispatch({type: c.ISSUE_BUSY, payload: {busy: true}});
    axios.post(window.serviceHost + '/v2/issues', JSON.stringify(issue), {headers: getHeaders()})
    .then((response) => {
      if (response.status == 201) {
        dispatch({type: c.ISSUE_ADD_SUCCESS, payload: {issue: response.data}});
      } else if (response.status == 200) {
        alert(response.data.message);
        dispatch({type: c.ISSUE_ADD_FAIL});
      }
    }).catch( (err) => {
      if (err.response.status == 400) {
        dispatch({type: c.ISSUE_BAD_REQUEST, payload: {errors: err.response.data}});
      }else {
        dispatch({type: c.ISSUE_ADD_FAIL});
      }
    });
  };
}

export function updateIssue (issue, operation) {
  console.log('updateIssue');
  return function (dispatch) {
    dispatch({type: c.ISSUE_BUSY, payload: {busy: true}});
    axios.patch(window.serviceHost + '/v2/issues/' + issue.id + '?operation=' + operation, JSON.stringify(issue), {headers: getHeaders()})
    .then((response) => {
      console.log(response);
      if (response.status == 200) {
        if ( 'message' in response.data) {
          alert(response.data.message);
          dispatch({type: c.ISSUE_EDIT_FAIL});
        } else {
          dispatch({type: c.ISSUE_EDIT_SUCCESS, payload: {issue: response.data}});
        }
      }
    }).catch( (err) => {
      console.log(err);
      if (err.response.status == 400) {
        dispatch({type: c.ISSUE_BAD_REQUEST, payload: {errors: err.response.data}});
      }else {
        dispatch({type: c.ISSUE_EDIT_FAIL});
      }
    });
  };
}

export function removeIssue (issue) {
  console.log('removeIssue');
  return function (dispatch) {
    console.log(issue);

    axios.delete(window.serviceHost + '/v2/issues/' + issue.id, {headers: getHeaders()})
    .then((response) => {
      console.log(response);
      dispatch({type: c.ISSUE_REMOVE_SUCCESS, payload: {issue: issue}});
    }).catch( (err) => {
      console.log(err);
      dispatch({type: c.ISSUE_REMOVE_FAIL});
    });
  };
}



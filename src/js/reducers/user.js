import { USER_CONSTANTS as c} from "../utils/constants";

const initialState = {
  loaded: false,
  authenticated: false,
  users: [],
  fetching: false,
  adding: false,
  editing: false,
  user: {},
  filter: {},
  sort: 'name:asc',
  toggleStatus: true
};

const handlers = { 
  [c.INITIALIZE_USER]: (_, action) => ({users: action.payload.users, loaded: true, toggleStatus: !_.toggleStatus}),
  [c.USER_AUTH_SUCCESS]: (_, action) => {
    let users = _.users;
    if (users.length == 0) {
      console.log('users not loaded');
      return ({});
    }
    let i = users.findIndex(u => u.email == action.payload.username);
    const user = users[i];
    window.sessionStorage.username = user.name;
    window.sessionStorage.email = user.email;
    window.sessionStorage.access_token = action.payload.data.access_token;
    window.sessionStorage.refresh_token = action.payload.data.refresh_token;
    window.sessionStorage.role = user.role;
    window.sessionStorage.userType = user.userType;
    return ({authenticated: true});
  },
  [c.USER_AUTH_FAIL]: (_, action) => ({authenticated: false}),
  [c.USER_ADD_FORM_TOGGLE]: (_, action) => ({adding: action.payload.adding}),
  [c.USER_ADD_SUCCESS]: (_, action) => {
    let users = _.users;
    users.push(action.payload.user);
    return ({adding: false,toggleStatus: !_.toggleStatus, users: users});
  },
  [c.USER_ADD_FAIL]: (_, action) => ({adding: false}),
  [c.USER_EDIT_FORM_TOGGLE]: (_, action) => ({editing: action.payload.editing, user: action.payload.user}),
  [c.USER_EDIT_SUCCESS]: (_, action) => {
    let users = _.users;
    let i = users.findIndex(u => u.id == action.payload.user.id);
    users[i] = action.payload.user;
    return ({editing: false,toggleStatus: !_.toggleStatus, users: users});
  },
  [c.USER_EDIT_FAIL]: (_, action) => ({editing: false}),
  [c.USER_REMOVE_SUCCESS]: (_, action) => {
    let users = _.users.filter(u => u.id != action.payload.user.id);
    return ({toggleStatus: !_.toggleStatus,users: users});
  },
  [c.USER_SORT]: (_, action) => ({sort: action.payload.sort, toggleStatus: !_.toggleStatus}),
  [c.USER_FILTER]: (_, action) => ({filter: action.payload.filter, toggleStatus: !_.toggleStatus})
};

export default function user (state = initialState, action) {
  let handler = handlers[action.type];
  if( !handler ) return state;
  return { ...state, ...handler(state, action) };
}

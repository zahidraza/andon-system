import { USER_CONSTANTS as c} from "../utils/constants";

const initialState = {
  authProgress: false,
  authenticated: false,
  loaded: false,
  busy: false,
  fetching: false,
  adding: false,
  editing: false,
  users: [],
  user: {},
  filter: {},
  sort: 'name:asc',
  toggleStatus: true,
  message: '',
  error: {}
};

const handlers = { 
  [c.INITIALIZE_USER]: (_, action) => ({users: action.payload.users, loaded: true, toggleStatus: !_.toggleStatus}),
  [c.USER_AUTH_PROGRESS]: (_, action) => ({authProgress: true, authenticated: false}),
  [c.USER_AUTH_SUCCESS]: (_, action) => {
    console.log("$$$$ Success $$$$$");
    sessionStorage.email = action.payload.username;
    window.sessionStorage.access_token = action.payload.data.access_token;
    window.sessionStorage.refresh_token = action.payload.data.refresh_token;
    //window.sessionStorage.session = true;
    return ({authProgress: false, authenticated: true});
  },
  [c.USER_AUTH_FAIL]: (_, action) => ({authProgress: false}),
  [c.USER_SEARCH_SUCCESS]: (_, action) => {
    let user = action.payload.user;
    sessionStorage.userId = user.id;
    sessionStorage.username = user.name;
    sessionStorage.role = user.role;
    sessionStorage.userType = user.userType;
    sessionStorage.level = user.level;
    sessionStorage.session = true;
    return ({busy: false});
  },
  [c.USER_ADD_FORM_TOGGLE]: (_, action) => ({adding: action.payload.adding, error:{}}),
  [c.USER_ADD_SUCCESS]: (_, action) => {
    let users = _.users;
    users.push(action.payload.user);
    return ({adding: false,toggleStatus: !_.toggleStatus, users: users, busy: false});
  },
  [c.USER_BAD_REQUEST]: (_, action) => {
    let error = {};
    action.payload.errors.forEach(err => {
      error[err.field] = err.message;
    });
    return ({error, busy: false});
  },
  [c.USER_ADD_FAIL]: (_, action) => ({adding: false, busy: false}),
  [c.USER_EDIT_FORM_TOGGLE]: (_, action) => ({editing: action.payload.editing, user: action.payload.user, error: {}}),
  [c.USER_EDIT_SUCCESS]: (_, action) => {
    let users = _.users;
    let i = users.findIndex(u => u.id == action.payload.user.id);
    users[i] = action.payload.user;
    return ({editing: false,toggleStatus: !_.toggleStatus, users: users, busy: false});
  },
  [c.USER_EDIT_FAIL]: (_, action) => ({editing: false, busy: false}),
  [c.USER_REMOVE_SUCCESS]: (_, action) => {
    let users = _.users.filter(u => u.id != action.payload.user.id);
    return ({toggleStatus: !_.toggleStatus,users: users});
  },
  [c.USER_SORT]: (_, action) => ({sort: action.payload.sort, toggleStatus: !_.toggleStatus}),
  [c.USER_FILTER]: (_, action) => ({filter: action.payload.filter, toggleStatus: !_.toggleStatus}),
  [c.USER_CHANGE_PASSWD]: (_, action) => ({message: action.payload.message}),
  [c.USER_BUSY]: (_, action) => ({busy: true})
};

export default function user (state = initialState, action) {
  let handler = handlers[action.type];
  if( !handler ) return state;
  return { ...state, ...handler(state, action) };
}

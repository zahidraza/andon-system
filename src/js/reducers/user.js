import { USER_CONSTANTS as c} from "../utils/constants";

const initialState = {
  authProgress: false,
  loaded: false,
  users: [],
  fetching: false,
  adding: false,
  editing: false,
  user: {},
  filter: {},
  sort: 'name:asc',
  toggleStatus: true,
  message: '',
  error: {}
};

const handlers = { 
  [c.INITIALIZE_USER]: (_, action) => ({users: action.payload.users, loaded: true, toggleStatus: !_.toggleStatus}),
  [c.USER_AUTH_PROGRESS]: (_, action) => ({authProgress: true}),
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
    window.sessionStorage.session = true;
    return ({authProgress: false});
  },
  [c.USER_AUTH_FAIL]: (_, action) => ({authProgress: false}),
  [c.USER_ADD_FORM_TOGGLE]: (_, action) => ({adding: action.payload.adding, error:{}}),
  [c.USER_ADD_SUCCESS]: (_, action) => {
    let users = _.users;
    users.push(action.payload.user);
    return ({adding: false,toggleStatus: !_.toggleStatus, users: users});
  },
  [c.USER_BAD_REQUEST]: (_, action) => {
    let error = {};
    action.payload.errors.forEach(err => {
      error[err.field] = err.message;
    });
    return ({error});
  },
  [c.USER_EDIT_FORM_TOGGLE]: (_, action) => ({editing: action.payload.editing, user: action.payload.user, error: {}}),
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
  [c.USER_FILTER]: (_, action) => ({filter: action.payload.filter, toggleStatus: !_.toggleStatus}),
  [c.USER_CHANGE_PASSWD]: (_, action) => ({message: action.payload.message})
};

export default function user (state = initialState, action) {
  let handler = handlers[action.type];
  if( !handler ) return state;
  return { ...state, ...handler(state, action) };
}

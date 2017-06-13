import { ISSUE_CONSTANTS as c} from "../utils/constants";

const initialState = {
  busy: false,
  adding: false,
  viewing: false,
  editing: false,
  issueSync: undefined,
  issues: [],
  filter: {},
  sort: 'name:asc',
  toggleStatus: true,
  error: {}
};

const handlers = { 
  [c.ISSUE_BUSY]: (_, action) => ({busy: action.payload.busy}),
  [c.ISSUE_ADD_FORM_TOGGLE]: (_, action) => ({adding: action.payload.adding, error:{}}),
  [c.ISSUE_VIEW_TOGGLE]: (_, action) => ({viewing: action.payload.viewing, error:{}}),
  [c.ISSUE_ADD_SUCCESS]: (_, action) => {
    let issues = _.issues;
    issues.push(action.payload.issue);
    return ({issues, adding: false,toggleStatus: !_.toggleStatus, busy: false});
  },
  [c.ISSUE_ADD_FAIL]: (_, action) => ({adding: false, busy: false}),
  [c.ISSUE_SYNC_SUCCESS]: (_, action) => {
    let issues = _.issues;
    const resp = action.payload.issue;
    _.issueSync = resp.issueSync;
    let idx;
    resp.issues.forEach(issue => {
      idx = issues.findIndex(i => i.id == issue.id);
      if (idx == -1)  issues.push(issue);
      else issues[idx] = issue;
    });
    return ({issues, toggleStatus: !_.toggleStatus, busy: false});
  },

  [c.ISSUE_SYNC_FAIL]: (_, action) => ({busy: false}),
  [c.ISSUE_EDIT_FORM_TOGGLE]: (_, action) => ({editing: action.payload.editing, issue: action.payload.issue, error: {}}),
  [c.ISSUE_EDIT_SUCCESS]: (_, action) => {
    let issues = _.issues;
    let i = issues.findIndex(u => u.id == action.payload.issue.id);
    issues[i] = action.payload.issue;
    return ({editing: false, viewing: false, toggleStatus: !_.toggleStatus, issues: issues, busy: false});
  },
  [c.ISSUE_EDIT_FAIL]: (_, action) => ({editing: false, viewing: false, busy: false}),
  [c.ISSUE_REMOVE_SUCCESS]: (_, action) => {
    let issues = _.issues.filter(u => u.id != action.payload.issue.id);
    return ({toggleStatus: !_.toggleStatus,issues: issues});
  },
  [c.ISSUE_BAD_REQUEST]: (_, action) => {
    let error = {};
    action.payload.errors.forEach(err => {
      error[err.field] = err.message;
    });
    return ({error, busy: false});
  },
  [c.ISSUE_SORT]: (_, action) => ({sort: action.payload.sort, toggleStatus: !_.toggleStatus}),
  [c.ISSUE_FILTER]: (_, action) => ({filter: action.payload.filter, toggleStatus: !_.toggleStatus})
};

export default function issue (state = initialState, action) {
  let handler = handlers[action.type];
  if( !handler ) return state;
  return { ...state, ...handler(state, action) };
}

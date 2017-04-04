import { useRouterHistory } from "react-router";
import { createHistory } from 'history';

const history = useRouterHistory(createHistory)({
  basename: '/ics-frontend'
});

module.exports = history;

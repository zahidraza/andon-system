import Main from "./components/Main";
import Dashboard1 from "./components/factory/Dashboard";
import Dashboard2 from "./components/city/Dashboard";
import Login from "./components/Login";
import Profile from "./components/Profile";
import User from "./components/city/User";
import UserAdd from "./components/city/UserAdd";
import UserEdit from "./components/city/UserEdit";
import Mapping1 from "./components/factory/Mapping";
import Mapping2 from "./components/city/Mapping";
import Problem from "./components/city/Problem";
import Report1 from "./components/factory/Report";
import Report2 from "./components/city/Report";
import Section from "./components/factory/Section";
import Department from "./components/factory/Department";
import IssueTracking from './components/city/IssueTracking';
import Download from './components/Download';

import Test from "./components/Test";

export default {
  path: '/',
  component: Main,
  indexRoute: {component: Login},
  childRoutes: [
    { path: 'dashboard1', component: Dashboard1},
    { path: 'dashboard2', component: Dashboard2},
    { path: 'user', component: User},
    { path: 'user/add', component: UserAdd},
    { path: 'user/edit', component: UserEdit},
    { path: 'mapping1', component: Mapping1},
    { path: 'mapping2', component: Mapping2},
    { path: 'problem', component: Problem},
    { path: 'profile', component: Profile},
    { path: 'report1', component: Report1},    
    { path: 'report2', component: Report2},
    { path: 'section', component: Section},
    { path: 'department', component: Department},
    { path: 'tracking', component: IssueTracking},
    { path: 'download', component: Download},
    { path: 'test', component: Test}
  ]
};

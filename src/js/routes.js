import Dashboard from "./components/Dashboard";
import Main from "./components/Main";
import Test from "./components/Test";
import Login from "./components/Login";

export default {
  path: '/',
  component: Main,
  indexRoute: {component: Login},
  childRoutes: [
    { path: 'test', component: Test},
    { path: 'dashboard2', component: Dashboard}
  ]
};

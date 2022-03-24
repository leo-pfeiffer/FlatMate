import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import axios from "axios";
import Buefy from "buefy";
import "buefy/dist/buefy.css";

axios.defaults.withCredentials = true;

// axios.defaults.baseURL = "http://localhost:8081/api/"; // todo
axios.defaults.baseURL = "https://gabbyblog.herokuapp.com/";

axios.interceptors.response.use(undefined, function(error) {
  if (error) {
    const originalRequest = error.config;
    if (error.response.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      store.dispatch("LogOut");
      return router.push("/login");
    }
  }
});

Vue.use(Buefy);

Vue.config.productionTip = false;

new Vue({
  router,
  store,
  render: (h) => h(App)
}).$mount("#app");

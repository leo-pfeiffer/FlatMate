import Vue from "vue";
import VueRouter from "vue-router";
import store from "@/store";
import Home from "@/views/Home.vue";
import Register from "@/views/Register";
import Login from "@/views/Login";
import User from "@/views/User";
import Admin from "@/views/Admin";
import Lobby from "@/views/Lobby";

Vue.use(VueRouter);

const routes = [
  {
    path: "/",
    name: "Home",
    component: Home,
    meta: { requiresAuth: true, requiresGroup: true },
  },
  {
    path: "/user",
    name: "User",
    component: User,
    meta: { requiresAuth: true, requiresGroup: true },
  },
  {
    path: "/admin",
    name: "Admin",
    component: Admin,
    meta: { requiresAuth: true, requiresAdmin: true, requiresGroup: true },
  },
  {
    path: "/lobby",
    name: "Lobby",
    component: Lobby,
    meta: { requiresAuth: true, requiresNoGroup: true },
  },
  {
    path: "/register",
    name: "Register",
    component: Register,
    meta: { guest: true },
  },
  {
    path: "/login",
    name: "Login",
    component: Login,
    meta: { guest: true },
  },
];
const router = new VueRouter({
  mode: "history",
  base: process.env.BASE_URL,
  routes,
});

// Prevent users from visiting pages that are admin only
router.beforeEach((to, from, next) => {
  if (to.matched.some((record) => record.meta.requiresAdmin)) {
    if (store.getters.isAdmin) {
      next();
      return;
    }
    next("/");
  } else {
    next();
  }
});

// Prevent users without group from visiting pages that require group membership
router.beforeEach((to, from, next) => {
  if (to.matched.some((record) => record.meta.requiresGroup)) {
    if (store.getters.StateGroup !== null) {
      next();
      return;
    }
    next("/lobby");
  } else if (to.matched.some((record) => record.meta.requiresNoGroup)) {
    if (store.getters.StateGroup === null) {
      next();
    }
    next("/");
  } else {
    next();
  }
});

// Prevent guests from visiting pages that require login
router.beforeEach((to, from, next) => {
  if (to.matched.some((record) => record.meta.requiresAuth)) {
    if (store.getters.isAuthenticated) {
      next();
      return;
    }
    next("/login");
  } else {
    next();
  }
});

// Routing for guest sites
router.beforeEach((to, from, next) => {
  if (to.matched.some((record) => record.meta.guest)) {
    if (store.getters.isAuthenticated) {
      next("/");
      return;
    }
    next();
  } else {
    next();
  }
});

export default router;

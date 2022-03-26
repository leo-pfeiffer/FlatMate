<template>
  <div id="app">
    <NavBar />
    <router-view />
  </div>
</template>
<script>
// @ is an alias to /src
import NavBar from "@/components/NavBar.vue";
export default {
  components: {
    NavBar,
  },
};
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  height: 100vh;
  width: 98%;
}

.center-screen {
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  min-height: 75vh;
}
@media (min-width: 500px) {
  .center-screen {
    padding-right: 50px;
    padding-left: 50px;
  }
}
@media (max-width: 500px) {
  .center-screen {
    padding-right: 10px;
    padding-left: 10px;
  }
}
.v-scroll {
  overflow-y: auto;
}
.h-scroll {
  overflow-x: auto;
}

.error-text {
  color: deeppink;
}

.button.is-disabled:hover {
  cursor: default;
}

.router-link-exact-active {
  color: #7957d5 !important;
}

.centered-table {
  border-collapse: separate;
  border-spacing: 10px;
}
.centered-table td {
  margin: 8px 8px 8px 8px;
  padding: 8px 8px 8px 8px;
  vertical-align: middle;
}
</style>

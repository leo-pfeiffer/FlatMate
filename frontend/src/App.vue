<template>
  <div id="app">
    <div id="mode-container">
      <ThemeToggle class="top-left" />
    </div>
    <NavBar />
    <router-view />
  </div>
</template>
<script>
// @ is an alias to /src
import NavBar from "@/components/NavBar.vue";
import ThemeToggle from "@/components/ThemeToggle";
export default {
  components: {
    ThemeToggle,
    NavBar,
  },
};
</script>

<!--suppress CssUnusedSymbol -->
<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  height: 100vh;
  width: 100%;
}

#mode-container {
  position: relative;
  width: 100%;
}

.top-left {
  position: absolute !important;
  top: 5px !important;
  right: 5px !important;
  z-index: 3 !important;
}

.simple-theme {
  height: 100%;
  width: 100%;
}

.fancy-theme {
  background: linear-gradient(315deg, #20bf55 0%, #01baef 74%);
  background-size: 400% 400%;
  animation: gradient 10s ease infinite;
  height: 100%;
  width: 100%;
}

@keyframes gradient {
  0% {
    background-position: 0 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0 50%;
  }
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

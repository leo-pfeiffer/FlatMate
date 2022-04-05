<template>
  <b-switch
    v-model="isFancy"
    passive-type="is-dark"
    type="is-warning"
    size="is-small"
    @input="toggleTheme"
  >
    <b-icon
      v-if="isFancy"
      icon="face-smile-beam"
      size="is-small"
      type="is-warning"
      class="btn-icon"
    />
    <b-icon
      v-else
      icon="face-smile-beam"
      size="is-small"
      type="is-dark"
      class="btn-icon"
    />
  </b-switch>
</template>

<script>
// Inspiration taken from here
//  https://codesandbox.io/s/immutable-monad-cotsz?file=/src/App.vue
import {
  makeExperiment,
  makeAdapter,
  experimentVariants,
} from "@/analytics/ab-testing";
import AlephBet from "alephbet";

let themeToggled;

export default {
  name: "ThemeToggle",
  data() {
    return {
      isFancy: false,
      userTheme: "fancy-theme",
    };
  },
  mounted() {
    // set up AB testing experiment
    if (process.env.NODE_ENV !== "test") {
      const name = "theme toggle";
      const variants = experimentVariants[name];
      const adapter = makeAdapter();
      const experiment = makeExperiment(name, variants, adapter);
      themeToggled = new AlephBet.Goal("theme toggled");
      experiment.add_goal(themeToggled);
    }
    this.init();
  },
  methods: {
    init() {
      const theme = document.getElementById("app").className;
      if (theme === "fancy-theme") {
        this.isFancy = true;
        this.userTheme = "fancy-theme";
      } else {
        this.isFancy = false;
        this.userTheme = "simple-theme";
      }
    },
    toggleTheme() {
      // register A/B test goal completion
      themeToggled.complete();
      if (this.userTheme === "fancy-theme") {
        this.setTheme("simple-theme");
      } else {
        this.setTheme("fancy-theme");
      }
    },
    setTheme(theme) {
      this.userTheme = theme;
      document.getElementById("app").className = theme;
    },
  },
};
</script>

<style scoped>
.btn-icon {
  vertical-align: middle;
  horiz-align: center;
}
</style>

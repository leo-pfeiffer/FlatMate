const AlephBet = require("alephbet");
const { trackAbTest } = require("@/api/api");

const makeAdapter = () => {
  return {
    experiment_start: async function (experiment, variant) {
      await trackAbTest(experiment.name, variant, "participate");
    },
    goal_complete: async function (experiment, variant, event_name) {
      await trackAbTest(experiment.name, variant, event_name);
    },
  };
};

const makeExperiment = (adapter) => {
  return new AlephBet.Experiment({
    name: "theme toggle",
    variants: {
      simple: {
        activate: function () {
          document.getElementById("app").className = "simple-theme";
        },
        weight: 50,
      },
      fancy: {
        activate: function () {
          document.getElementById("app").className = "fancy-theme";
        },
        weight: 50,
      },
    },
    tracking_adapter: adapter,
  });
};

module.exports = {
  makeExperiment: makeExperiment,
  makeAdapter: makeAdapter,
};

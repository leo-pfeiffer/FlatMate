const AlephBet = require("alephbet");
const { trackAbTest } = require("@/api/api");

/**
 * Wrapper for an A/B testing adapter for AlephBet experiments.
 * The adapter posts experiment events to the appropriate endpoint
 * of the backend REST api.
 * */
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

/**
 * Set up a new A/B testing experiment with AlephBet
 * @param name Name of the experiment
 * @param variants Object of the experiment variants
 * @param adapter Adapter of the experiment
 * */
const makeExperiment = (name, variants, adapter) => {
  return new AlephBet.Experiment({
    name: name,
    variants: variants,
    tracking_adapter: adapter,
  });
};

// Experiment variant presets
const experimentVariants = {
  "theme toggle": {
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
  "pay all bills": {
    active: {
      activate: function () {},
      weight: 50,
    },
    inactive: {
      activate: function () {
        document.getElementById("pay-all-bills").remove();
      },
      weight: 50,
    },
  },
};

module.exports = {
  makeExperiment: makeExperiment,
  makeAdapter: makeAdapter,
  experimentVariants: experimentVariants,
};

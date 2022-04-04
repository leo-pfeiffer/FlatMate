const AlephBet = require("alephbet");

const makeAdapter = () => {
  return {
    experiment_start: function (experiment, variant) {
      console.log(experiment, variant);
      // keen_client.addEvent(experiment.name, {variant: variant, event: 'participate'});
    },
    goal_complete: function (experiment, variant, event_name, _props) {
      console.log(experiment, variant, event_name, _props);
      // keen_client.addEvent(experiment.name, {variant: variant, event: event_name});
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

Array.prototype.max = function () {
    return Math.max.apply(null, this);
};

Array.prototype.min = function () {
    return Math.min.apply(null, this);
};

var colors = ['red', 'green', 'blue', 'black', 'violet', 'brown', 'pink'];
var graphNodes = [];
var chart = { destroy: () => { } };

function renderGraph() {
    var ch = document.getElementById('chart');
    var ctx = ch.getContext('2d');

    if (graphNodes.length < 1) {
        ch.style.display = 'none';
        return;
    }
    ch.style.display = "";
    var labels = graphNodes[0].data.map(er => new Date(er.timestamp)).map(d => ("0" + d.getHours()).slice(-2) + ":" + ("0" + d.getMinutes()).slice(-2));

    chart.destroy();
    chart = new Chart(ctx, {
        // The type of chart we want to create
        type: 'line',

        // The data for our dataset
        data: {
            labels: labels,
            datasets: graphNodes.map(el => ({
                label: el.name,
                data: el.data.map(er => er.value)
            }))
        },

        // Configuration options go here
        options: {}
    });
}
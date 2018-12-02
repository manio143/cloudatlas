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
    let dateFormat = interval >= 60 ?
        d => ("0" + d.getHours()).slice(-2) + ":" + ("0" + d.getMinutes()).slice(-2)
        : d => ("0" + d.getHours()).slice(-2) + ":" + ("0" + d.getHours()).slice(-2) + ":" + ("0" + d.getSeconds()).slice(-2);
    var timestamps = graphNodes.map(el => el.data.map(er => new Date(er.timestamp)));
    var labels = timestamps.reduce((acc, curr) => curr.length > acc.length ? curr : acc).map(dateFormat);

    chart.destroy();
    chart = new Chart(ctx, {
        // The type of chart we want to create
        type: 'line',

        // The data for our dataset
        data: {
            labels: labels,
            datasets: graphNodes.map(el => {
                let hash = [].reduce.call(el.name, function (hash, i) { var chr = i.charCodeAt(0); hash = ((hash << 5) - hash) + chr; return hash | 0; }, 0)
                return ({
                    label: el.name,
                    data: el.data.map(er => er.value),
                    borderColor: colorOfHash(hash),
                    backgroundColor: colorOfHash(hash, 0.3)
                })
            })
        },

        // Configuration options go here
        options: {}
    });
}

function colorOfHash(hash, a) {
    if (a == undefined) a = 1;
    var r = (hash >> 16) % 256;
    var g = (hash >> 8) % 256;
    var b = hash % 256;
    return 'rgba(' + r + ", " + g + ", " + b + "," + a + ")";
}
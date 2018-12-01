Array.prototype.max = function () {
    return Math.max.apply(null, this);
};

Array.prototype.min = function () {
    return Math.min.apply(null, this);
};

var colors = ['red', 'green', 'blue', 'black', 'violet', 'brown', 'pink'];
var graphNodes = [];
function renderGraph() {
    $('#labels').empty();
    $('#visualisation').empty();
    var vis = d3.select("#visualisation"),
        WIDTH = 1000,
        HEIGHT = 500,
        MARGINS = {
            top: 20,
            right: 20,
            bottom: 20,
            left: 50
        },
        minTime = Array.from(graphNodes.values()).map(el => el.data.map(er => er.timestamp).min()).min(),
        maxTime = Array.from(graphNodes.values()).map(el => el.data.map(er => er.timestamp).max()).max(),
        xScale = d3.scale.linear().range([MARGINS.left, WIDTH - MARGINS.right]).domain([minTime, maxTime]),
        yScale = d3.scale.linear().range([HEIGHT - MARGINS.top, MARGINS.bottom]).domain([0, 100]),
        xAxis = d3.svg.axis()
            .scale(xScale),
        yAxis = d3.svg.axis()
            .scale(yScale)
            .orient("left");

    vis.append("svg:g")
        .attr("class", "x axis")
        .attr("transform", "translate(0," + (HEIGHT - MARGINS.bottom) + ")")
        .call(xAxis);
    vis.append("svg:g")
        .attr("class", "y axis")
        .attr("transform", "translate(" + (MARGINS.left) + ",0)")
        .call(yAxis);
    var lineGen = d3.svg.line()
        .x(function (d) {
            return xScale(d.timestamp);
        })
        .y(function (d) {
            return yScale(d.value);
        })
        .interpolate("basis");

    if (graphNodes.length == 0)
        document.getElementById('visualisation').style.display = "none";
    else
        document.getElementById('visualisation').style.display = "";

    // DO TEGO MIEJSCA NIE MA PROBLEMU
    
    let colorIndex = 0;
    for (var node of graphNodes) {
        vis.append('svg-path')      //ALE TE LINIE SIE NIE POJAWIAJĄ
            .attr('d', lineGen(node.data))
            .attr('stroke', colors[colorIndex])
            .attr('stroke-width', 2)
            .attr('fill', 'none');
        $('#labels').append("<span style='color: " + colors[colorIndex] + ";' >" + node.name + "&nbsp;</span>");
        colorIndex = (colorIndex + 1) % colors.length;
    }
}
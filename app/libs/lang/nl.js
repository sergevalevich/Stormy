function join_with_shared_prefix(a, b, joiner) {
  var i = 0;

  while(i !== a.length &&
        i !== b.length &&
        a.charCodeAt(i) === b.charCodeAt(i))
    ++i;

  while(i && a.charCodeAt(i - 1) !== 32)
    --i;

  return a.slice(0, i) + a.slice(i) + joiner + b.slice(i);
}

function today_variants(scope, a, b){
  return scope.indexOf("starting-continuing-until") !== -1 ? a : b;
}

function weekdays(scope, day){
  return (scope.indexOf("through") !== -1 ? "van " : "op ") + day;
}

module.exports = require("../template")({
  "clear": "helder",
  "no-precipitation": "geen neerslag van betekenis",
  "mixed-precipitation": "wisselende neerslag",
  "very-light-precipitation": "lichte neerslag",
  "light-precipitation": "lichte neerslag",
  "medium-precipitation": "neerslag",
  "heavy-precipitation": "zware neerslag",
  "very-light-rain": "motregen",
  "light-rain": "lichte regen",
  "medium-rain": "regen",
  "heavy-rain": "zware regenbuien",
  "very-light-sleet": "lichte ijzel",
  "light-sleet": "lichte ijzel",
  "medium-sleet": "ijzel",
  "heavy-sleet": "zware ijzel",
  "very-light-snow": "lichte sneeuwbuien",
  "light-snow": "lichte sneeuwbuien",
  "medium-snow": "sneeuw",
  "heavy-snow": "zware sneeuwbuien",
  "light-wind": "weinig wind",
  "medium-wind": "veel wind",
  "heavy-wind": "zware windstoten",
  "low-humidity": "lage luchtvochtigheid",
  "high-humidity": "hoge luchtvochtigheid",
  "fog": "mist",
  "light-clouds": "licht bewolkt",
  "medium-clouds": "overwegend bewolkt",
  "heavy-clouds": "zwaar bewolkt",
  "today-morning":  function() {
    return today_variants(this, "de ochtend", "vanochtend");
  },
  "later-today-morning": "later vanochtend",
  "today-afternoon": function() {
    return today_variants(this, "de middag", "vanmiddag");
  },
  "later-today-afternoon": "later vanmiddag",
  "today-evening": function() {
    return today_variants(this, "de avond", "vanavond");
  },
  "later-today-evening": "later vanavond",
  "today-night": function() {
    return today_variants(this, "de nacht", "vannacht");
  },
  "later-today-night": "later vannacht",
  "tomorrow-morning": "morgenochtend",
  "tomorrow-afternoon": "morgenmiddag",
  "tomorrow-evening": "morgenavond",
  "tomorrow-night": "morgennacht",
  "morning": "de ochtend",
  "afternoon": "de middag",
  "evening": "de avond",
  "night": "de nacht",
  "today": "vandaag",
  "tomorrow": "morgen",
  "sunday": "op zondag",
  "monday": function(){ return weekdays(this, "maandag"); },
  "tuesday": function(){ return weekdays(this, "dinsdag"); },
  "wednesday":function(){ return weekdays(this, "woensdag"); }, 
  "thursday": function(){ return weekdays(this, "donderdag"); },
  "friday": function(){ return weekdays(this, "vrijdag"); },
  "saturday": function(){ return weekdays(this, "zaterdag"); },
  "minutes": "$1 min.",
  "fahrenheit": "$1\u00B0F",
  "celsius": "$1\u00B0C",
  "inches": "$1 inch",
  "centimeters": "$1 cm",
  "less-than": "minder dan $1",
  "and": function(a, b) {
    return join_with_shared_prefix(
      a,
      b,
      a.indexOf(",") !== -1 ? ", en " : " en "
    );
  },
  "through": function(a, b) {
    return join_with_shared_prefix(a, b, " tot ");
  },
  "with": "$1 met $2",
  "range": "$1 tot $2",
  "parenthetical": "$1 ($2)",
  "for-hour": "het komende uur $1",
  "starting-in": "over $2 $1",
  "stopping-in": "$1, stopt over $2",
  "starting-then-stopping-later": "$1 begint over $2 en stopt weer $3 later",
  "stopping-then-starting-later": "$1 stopt over $2 maar begint $3 later opnieuw",
  "for-day": "$1 gedurende de dag",
  "starting":  function(condition, time) {
    if(this[1] === "starting")
      return "in " + time + " " + condition;

    else if(this[1] === "and")
      return time + " " + condition;

    else
      return condition + " " + time;
  },
  "until": "$1 tot $2",
  "until-starting-again": "$1 tot $2 en $3 weer opnieuw",
  "starting-continuing-until": "$1 vanaf $2 dat door blijft gaan tot $3",
  "during": function(condition, time) {
    if(this[1] === "and")
      return time + " " + condition;

    else if(this[1] === "with")
      return condition + " " + time;

    else
      return condition + " gedurende " + time;
  },
  "for-week": "de hele week $1",
  "over-weekend": "$1 dit weekend",
  "temperatures-peaking": "een maximum temperatuur van $1 $2",
  "temperatures-rising": "temperaturen stijgend tot $1 $2",
  "temperatures-valleying": "een minimum temperatuur van $1 $2",
  "temperatures-falling": "temperaturen dalend tot $1 $2",
  /* Capitalize the first letter of every word, except if that word is
   * "and". (This is a very crude bastardization of proper English titling
   * rules, but it is adequate for the purposes of this module.) */
  "title": function(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
  },
  /* Capitalize the first word of the sentence and end with a period. */
  "sentence": function(str) {
    /* Capitalize. */
    str = str.charAt(0).toUpperCase() + str.slice(1);

    /* Add a period if there isn't already one. */
    if(str.charAt(str.length - 1) !== ".")
      str += ".";

    return str;
  }
});

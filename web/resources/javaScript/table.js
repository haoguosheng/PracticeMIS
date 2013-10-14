function changeColor (str,classStr) {
    var selector = "."+str + " tr";
    $(selector).hover(function (){
            $(this).addClass(classStr);
    },function () {
        $(this).removeClass(classStr);
        
    });
}

$(document).ready(function () {
    changeColor ("companyInfo","classOfTable");
});

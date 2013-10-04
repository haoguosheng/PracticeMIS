(function(){
    var db = document.body;
    var Calendar = function(opts){
        return new Calendar.prototype.init(opts);
    };
    Calendar.prototype = {
        constructor:Calendar,
        init:function(opts){
            this.opts = extend({
                target:'',
                initial:'2012/09/27',
                format:'YYYY-MM-DD',
                callback:function(){}
            },opts||{});
            var _this = this;
            this.target = $(this.opts.target);
            addEvent(this.target,'click',function(){_this.create();});
        },
        create:function(){
            var de = this.target.getAttribute('data-date');
            this.defaultValue = this.defaultValue || (de && new Date(de)) || (this.initial ? new Date(this.initial) : new Date());
            var year = this.defaultValue.getFullYear(),month = this.defaultValue.getMonth(),date =this.defaultValue.getDate(), _this = this;
            var days = this.calculateDays(year,month),fragment = document.createDocumentFragment(),ul = document.createElement('ul');
            this.close();
            this.wrapper = createEl('<div id="date_wrapper"><div id="date_header"><div id="yearBox"><span id="prevYear">«</span><em>'+year+'</em><span id="nextYear">»</span></div><div id="monthBox"><span id="prevMonth">«</span><em>'+(month+1)+'</em><span id="nextMonth">»</span></div></div><div id="date_weekBox"><ul><li>日</li><li>一</li><li>二</li><li>三</li><li>四</li><li>五</li><li>六</li></ul></div><div id="date_dateBox"></div></div>',db);
             
            for(var i=0,len=days.length;i<len;i++){
                var li = document.createElement('li');
                var y = days[i].getFullYear(),m = days[i].getMonth(),d = days[i].getDate();
                var f = m == month;
                var fl = year == y && month == m && date == d;
                li.setAttribute('data-date',days[i]);
                li.innerHTML = d;
                addClass(li,f ? 'black' : 'gray');
                fl && addClass(li,'current');
                fragment.appendChild(li);
            }
            ul.appendChild(fragment);
            $('date_dateBox').appendChild(ul);
             
            setPosition(this.wrapper,this.target);
             
            addEvent($('prevYear'),'click',function(){
                _this.defaultValue.setMonth(_this.defaultValue.getMonth()-12,date);
                _this.create();
            });
            addEvent($('nextYear'),'click',function(){
                _this.defaultValue.setMonth(_this.defaultValue.getMonth()+12,date);
                _this.create();
            });
            addEvent($('prevMonth'),'click',function(){
                _this.defaultValue.setMonth(_this.defaultValue.getMonth()-1,date);
                _this.create();
            });
            addEvent($('nextMonth'),'click',function(){
                _this.defaultValue.setMonth(_this.defaultValue.getMonth()+1,date);
                _this.create();
            });
            addEvent(ul,'click',function(e){
                e = e || window.event;
                var target = e.target || e.srcElement;
                if(hasClass(target,'black')){
                    var date = target.getAttribute('data-date');
                    _this.target.setAttribute('data-date',date);
                    _this.defaultValue = new Date(date);
                    if(_this.target.type == 'text') _this.target.value = formatDate(date,_this.opts.format);
                    _this.opts.callback && _this.opts.callback(date);
                    _this.close();
                };
            });         
        },
        set:function(d){
            this.defaultValue = d ? new Date(d) : new Date();
            this.create();
        },
        get:function(){
            return this.defaultValue;
        },
        close:function(){
            if(!this.wrapper) return;
            this.wrapper.parentNode.removeChild(this.wrapper);
            this.wrapper = null;
        },
        trigger:function(){
            this.create();
        },
        calculateDays:function(y,m){
            var offset, dFirstDay = new Date(y, m, 1), dLastDay = new Date(y, m + 1, 0), arr = [];
            dFirstDay.setMonth(m, 1 - dFirstDay.getDay());
            dLastDay.setMonth(m, dLastDay.getDate() + 6 - dLastDay.getDay());
            offset = parseInt((dLastDay - dFirstDay) / (1000*60*60*24)) + 1;
            var _y = dFirstDay.getFullYear(), _m = dFirstDay.getMonth(), _d = dFirstDay.getDate();
            for(var i=0; i<offset; i++){
                var d = new Date(_y, _m, _d + i);
                arr.push(d);
            }
            return arr;
        }
    };
    Calendar.prototype.init.prototype = Calendar.prototype;
    window.Calendar = Calendar;
     
     
    function $(id){
        return typeof id == 'string' ? document.getElementById(id) : id;
    };
    function createEl(str,parent){
        var div = document.createElement('div'),el;
        div.innerHTML = str;
        el = div.firstChild;
        parent && parent.appendChild(el);
        return el;
    };
    function extend(t,s){
        for(var i in s ) t[i] = s[i];
        return t;
    };
    function getElementPos(el){
        var x = 0,y=0;
        if(el.getBoundingClientRect){
            var pos = el.getBoundingClientRect();
            var d_root = document.documentElement,db = document.body;
            x = pos.left + Math.max(d_root.scrollLeft,db.scrollLeft) - d_root.clientLeft;
            y = pos.top + Math.max(d_root.scrollTop,db.scrollTop) - d_root.clientTop;
        }else{
            while(el != db){
                x += el.offsetLeft;
                y += el.offsetTop;
                el = el.offsetParent;
            };
        };
        return {
            x:x,
            y:y
        };
    };
    function setPosition(target,reference){
        var pos = getElementPos(reference);
        var left = pos.x,top = pos.y;
        var width = reference.offsetWidth,height = reference.offsetHeight;
        var w = target.offsetWidth,h = target.offsetHeight;
        var st = Math.max(document.documentElement.scrollTop,document.body.scrollTop),sl = Math.max(document.documentElement.scrollLeft,document.body.scrollLeft);
        var cw =  document.documentElement.clientWidth,ch = document.documentElement.clientHeight;
        if(ch+st-top-height-1 > h ){
            target.style.top = top + height + 1 + 'px';
        }else{
            target.style.top = top - h - 1 + 'px';
        };
        if(cw+sl-left-width > w){
            target.style.left = left + 'px';
        }else{
            target.style.left = left + width - w + 'px';
        };
    };
    function addEvent(el,type,fn){
        if(typeof el.addEventListener != 'undefined'){
            el.addEventListener(type,fn,false);
        }else if(typeof el.attachEvent != 'undefined'){
            el.attachEvent('on'+type,fn);
        }else{
            el['on'+type] = fn;
        };
    };
    function formatDate(t,tpl){
        var strs=[], w, keys, year, val,t= t ? new Date(t) : new Date();
        w = 'FullYear,Month,Date'.split(',');
        keys = [/YYYY/g, /YY/g, /MM/g, /M/g, /DD/g, /D/g];
        for (var i = 0; i < 3; i++) {
            val = t['get' + w[i]]() + (w[i] === 'Month' ? 1 : 0);
            strs.push(('0' + val).slice( - 2), val);
        };
        year = [strs[1], strs[0]].concat(strs.slice(2));
        for (var i = 0; i < 6; i++) {
            tpl = tpl.replace(keys[i], year[i]);
        };
        return tpl;
    };
    function clear(str){
        return str.replace(/^\s+|\s+$/g,'').replace(/\s+/,' ');
    };
    function hasClass(el,oClass){
        return (' '+el.className+' ').indexOf(' '+oClass+' ') > -1;
    };
    function addClass(el,oClass){
        if(hasClass(el,oClass)) return;
        var c = el.className;
        el.className = clear(c? c + ' '+oClass : oClass);
    };
    function removeClass(el,oClass){
        if(!hasClass(el,oClass)) return;
        var c = el.className;
        el.className = clear((' '+c+' ').replace(' '+oClass+' ',''));
    };
})();
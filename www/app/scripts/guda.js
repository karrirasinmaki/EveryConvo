(function(window, doc) {
    
    var log = console.log.bind(console);

    var now = function() {
        return new Date().getTime();
    }

    var ajax = function(type, url, params) {
        var doneFn = undefined;
        this.done = function(fn) { doneFn = fn; };

        var xmlhttp = new XMLHttpRequest();
        xmlhttp.onreadystatechange = function() {
            if(xmlhttp.readyState === 4) {
                if(typeof doneFn === "function") doneFn(xmlhttp.responseText);
            }
        }
        xmlhttp.open(type, url, true);
        if(type === "post") xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        xmlhttp.send(params || "");

        return this;
    }
    var getAjax = function(url) { return ajax("get", url); }
    var postAjax = function(url) {
        var strings = url.split("?");
        return ajax("post", strings[0], strings[1]);
    }
    
    var dom = {
        addClass: function(className, element) {
            element.className += " " + className;
        },
        append: function(elementToAppend, element) {
            if( this.extendsDom ) element = this.element;
            if( elementToAppend.extendsDom ) elementToAppend = elementToAppend.element;
            element.appendChild( elementToAppend );
            
            if( this.extendsDom ) return this;
        }
    };
    
    var Widget = function(params) {
        this.init(params);
    };
    Widget.prototype = dom;
    Widget.prototype.init = function(params, tagName) {
        if( !tagName ) {
            if( this.element ) tagName = this.element.tagName || "div";
            else tagName = "div";
        }
        
        this.extendsDom = true;
        this.element = doc.createElement( tagName );
        
        for(var k in params) {
            this.element[k] = params[k];
        }
    };
    
    var View = function(params) {
        this.init( params );
        doc.body.appendChild( this.element );
    };
    View.prototype = new Widget;
    
    var Form = function(params) {
        this.init( params, "form" );
    };
    Form.prototype = new Widget;
    
    var TextArea = function(params) {
        this.init( params, "textarea" );
    };
    TextArea.prototype = new Widget;
    
    var Button = function(params) {
        this.init( params, "button" );
    };
    Button.prototype = new Widget;
    
    var SidebarWidget = function(params) {
        this.init( params );
    };
    SidebarWidget.prototype = new Widget;
    
    
    window["g"] = {
        log: log,
        now: now,
        ajax: ajax,
        getAjax: getAjax,
        postAjax: postAjax,
        dom: dom,
        Widget: Widget,
        View: View,
        Form: Form,
        TextArea: TextArea,
        Button: Button,
        SidebarWidget: SidebarWidget
    };
    
})(window, document);

define(function() {
    return window["g"];
});
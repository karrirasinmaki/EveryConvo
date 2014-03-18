(function(window, doc) {
    
    var log = console.log.bind(console);

    var now = function() {
        return new Date().getTime();
    }

    var ajax = function(type, url, params, responseType) {
        var doneFn = undefined;
        this.done = function(fn) { doneFn = fn; };

        var xmlhttp = new XMLHttpRequest();
        xmlhttp.onreadystatechange = function() {
            if(xmlhttp.readyState === 4) {
                if(typeof doneFn === "function") doneFn(xmlhttp.responseText);
            }
        }
        xmlhttp.open(type, url, true);
        xmlhttp.setRequestHeader("Content-type", "application/json");
        xmlhttp.setRequestHeader('Accept', 'application/json');
        if(type === "post") xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        xmlhttp.send(params || "");

        return this;
    }
    var getAjax = function(url) { return ajax("get", url); }
    var getJSON = function(url) { return ajax("get", url); }
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
    
    var Input = function(params) {
        this.init( params, "input" );
    };
    Input.prototype = new Widget;
    
    var Button = function(params) {
        this.init( params, "button" );
    };
    Button.prototype = new Widget;
    
    var MediaWidget = function(params) {
        if( !params || !params.mediaURL ) return;
        
        var mediaURL = params.mediaURL;
        switch( mediaURL.substr( mediaURL.lastIndexOf(".") + 1 ).toLowerCase() ) {
            case "jpg":
                this.init( params, "img" );
                this.element.src = mediaURL;
                break;
        }
        
    };
    MediaWidget.prototype = new Widget;
    
    var SidebarWidget = function(params) {
        this.init( params );
    };
    SidebarWidget.prototype = new Widget;
    
    
    window["g"] = {
        log: log,
        now: now,
        ajax: ajax,
        getAjax: getAjax,
        getJSON: getJSON,
        postAjax: postAjax,
        dom: dom,
        Widget: Widget,
        View: View,
        Form: Form,
        TextArea: TextArea,
        Input: Input,
        Button: Button,
        MediaWidget: MediaWidget,
        SidebarWidget: SidebarWidget
    };
    
})(window, document);

define(function() {
    return window["g"];
});
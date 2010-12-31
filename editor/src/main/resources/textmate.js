TextMate = {};
TextMate.port = @port@;
TextMate.windowId = @windowId@;
TextMate.callback = {};
TextMate.callbackStatus = {};
TextMate.handleIdx = 0;
TextMate.system = function (cmd, handler) {
    if (handler == null) {
        var ret = _system("exec", cmd, null);
        return {
            outputString: ret[1],
            errorString: ret[2],
            status: ret[3]
        };
    } else {
        var callbackName = 'fn' + (++TextMate.handleIdx);
        TextMate.callbackStatus[callbackName] = {
            cancel: function() { _system('cancel', null, callbackName); },
            close: function() { _system('close', null, callbackName); },
            write: function(s) { _system('write', s, callbackName); },
            outputString: '',
            errorString: '',
            status: null,
            onreadoutput: null,
            onreaderror: null,
            id: null
        };
        TextMate.callback[callbackName] = function(ev, os, es, st) {
            var cb = TextMate.callback[callbackName],
                cbs = TextMate.callbackStatus[callbackName];
            if (ev == 1) {
                cbs.status = st;
                cbs.outputString = os;
                cbs.errorString = es;
                handler({ outputString: os, errorString: es, status: st });

                TextMate.callback[callbackName] = null;
                TextMate.callbackStatus[callbackName] = null;
            } else if (ev == 2) {
                if (cbs.onreadoutput) cbs.onreadoutput(os);
            } else if (ev == 3) {
                if (cbs.onreaderror) cbs.onreaderror(os);
            }
        };
        _system("exec", cmd, callbackName);
        return TextMate.callbackStatus[callbackName];
    }
};

if (typeof _system == "undefined") {
    TextMate._xhr = {};
    _system = function(ev, cmd, handler) {
        if (ev == "exec") {
            var xhr = new XMLHttpRequest();
            xhr.open('POST', 'http://localhost:' + TextMate.port + '/cmd/exec?cmd=' + escape(cmd), handler != null);
            if (handler == null) {
                xhr.send(null);
                return [ xhr.responseText, null, xhr.getResponseHeader('X-ResponseCode') ];
            } else {
                xhr.onreadystatechange = function() {
                    if (xhr.readyState == 4 && xhr.status != 404) {
                        TextMate._xhr[handler] = null;
                        TextMate.callback[handler](1, [ xhr.responseText, null, xhr.getResponseHeader('X-ResponseCode') ]);
                    }
                };
                xhr.send(null);
                TextMate._xhr[handler] = xhr;
                return [ null, null, null ];
            }
        } else if (ev == "cancel") {
            TextMate._xhr[handler].abort();
        } else if (ev == "close") {
            // Do nothing
        } else if (ev == "write") {
            TextMate._xhr[handler].send(cmd);
        }
    };
}

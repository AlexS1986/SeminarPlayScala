function deleteTask ( id, token ) {
          // build and send new request on client side
          var req = new XMLHttpRequest ( ) ;
          req.open ( "delete", "/tasks/" + id ) ;
          req.setRequestHeader('Csrf-Token', token );
          // event load fired when req completes successfully
          req.onload = function ( e ) {
          if ( req.status = 200 ) {
              document.location.reload ( true ) ;
            }
          };
          req.send ( ) ;
}
const args = require('yargs').argv;
var newman = require('newman'),
    fs = require('fs');
fs.readdir(args.folder, function (err, files) {
  if (!err) {
    files = files.filter(function (file) {
      return (/^((?!(package(-lock)?))|.+)\.json/).test(file);
    });

    files.forEach(function (file) {
      newman.run({
        collection: require(`${args.folder}${file}`),
        globals: {
          "id": "683ad527-9279-f62f-ff8d-663df9026ca8",
          "name": "Postman Globals",
          "values": [
            {
              "key": "host",
              "value": `${args.host}`,
              "enabled": true,
              "type": "text"
            },
			{
			  "key": "keycloack",
			  "value": `${args.keycloack}`,
			  "enabled": true,
			  "type": "text"
			}
          ],
          "_postman_variable_scope": "globals"
        },
		reporters: ['junit', 'cli', 'html'],
        reporter: {
          junit: {
            export: `./newman/test-result-${file}.xml`
          },
          html: {
            export: `./newman/index.html`,
            template: './cicd/template/customTemplate.hbs'
          }
        }
        // },
        // reporter: {
        //   html: {
        //     export: `./newman/index.html`,
        //     template: './cicd/template/customTemplate.hbs'
        //   }
        // }
      })
    });
  }
});

module.exports = function (grunt) {
    grunt.initConfig({
        less: {
            // 编译
            compile: {
                files: {
                    'assets/css/main.css': 'assets/css/main.less'
                }
            },
            // 压缩
            yuicompress: {
                files: {
                    'assets/css/main-min.css': 'assets/css/main.css'
                },
                options: {
                    yuicompress: true
                }
            }
        },
        watch: {
            scripts: {
                files: ['assets/css/*.less'],
                tasks: ['less']
            }
        }
    });
 
    grunt.loadNpmTasks('grunt-contrib-less');
    grunt.loadNpmTasks('grunt-contrib-watch');
 
    grunt.registerTask('default', ['less', 'watch']);
 
};

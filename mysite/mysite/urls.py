from django.conf.urls import patterns, include, url

from mysite.view import hello,time,timeplus,loadtest
# from django.contrib import admin
# admin.autodiscover()

urlpatterns = patterns('',
    ('^hello/$',hello),    
    # Examples:
    url(r'^$',loadtest),
    # url(r'^blog/', include('blog.urls')),

    # url(r'^admin/', include(admin.site.urls)),
    # url(r'^admin/', include(admin.site.urls)),
    ('^success/$',time),
    (r'^time/plus/(\d{1,2})/$',timeplus),
)

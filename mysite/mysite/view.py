from django.http import HttpResponse,Http404
from django.template import Template, Context
from django.template.loader import get_template
import datetime

def hello(request):
    return HttpResponse("Hello Bor!")

def time(requset):
    now = datetime.datetime.now()
    t = get_template('time.html')
    html = t.render(Context({'current_date':now}))
    return HttpResponse(html)

def timeplus(requse,offset):
    try:
       t = int(offset)
    except ValueError:
       raise Http404()
    dt = datetime.datetime.now() + datetime.timedelta(hours=t)
    k = get_template('timeplus.html')
    html = k.render(Context({'interval':t,'realtime':dt}))
    return HttpResponse(html)

def loadtest(request):
    t = get_template('QQlogin.html')
    html = t.render(Context())
    return HttpResponse(html)

#def loadtest1(request):
#    t = get_template('test.html')
#    html = t.render(Context())
#    return HttpResponse(html)


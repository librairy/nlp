FROM python:3.8
COPY requirements.sh /app/
RUN /app/requirements.sh
COPY *.py /app/
WORKDIR /app
ENTRYPOINT ["python"]
CMD ["app.py"]

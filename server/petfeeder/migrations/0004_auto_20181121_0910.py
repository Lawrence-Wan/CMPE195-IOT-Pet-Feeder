# Generated by Django 2.1 on 2018-11-21 09:10

from django.db import migrations, models
import django.utils.timezone


class Migration(migrations.Migration):

    dependencies = [
        ('petfeeder', '0003_auto_20181121_0907'),
    ]

    operations = [
        migrations.AlterField(
            model_name='petconsumptionaction',
            name='time',
            field=models.DateTimeField(default=django.utils.timezone.now),
        ),
    ]

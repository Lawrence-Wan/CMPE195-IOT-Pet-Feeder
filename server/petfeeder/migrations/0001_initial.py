# Generated by Django 2.0.2 on 2018-04-16 03:55

from django.conf import settings
import django.contrib.auth.models
from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    initial = True

    dependencies = [
        migrations.swappable_dependency(settings.AUTH_USER_MODEL),
        ('auth', '0009_alter_user_last_name_max_length'),
    ]

    operations = [
        migrations.CreateModel(
            name='FoodDispenserAction',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('time', models.DateTimeField()),
                ('cups', models.FloatField(default=0)),
                ('command', models.PositiveIntegerField(default=2)),
            ],
        ),
        migrations.CreateModel(
            name='Pet',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('chip_id', models.BigIntegerField()),
                ('pet_type', models.CharField(blank=True, default='', max_length=30)),
                ('pet_breed', models.CharField(blank=True, default='', max_length=30)),
                ('name', models.CharField(blank=True, default='', max_length=30)),
                ('birthday', models.DateField(blank=True, default=None)),
            ],
        ),
        migrations.CreateModel(
            name='PetConsumptionAction',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('time', models.DateTimeField()),
                ('mass', models.PositiveIntegerField(default=0)),
            ],
        ),
        migrations.CreateModel(
            name='PetFeeder',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('serial_id', models.CharField(max_length=36, unique=True)),
                ('setting_cup', models.PositiveIntegerField(default=0)),
                ('setting_interval', models.PositiveIntegerField(default=0)),
                ('setting_closure', models.BooleanField(default=False)),
            ],
        ),
        migrations.CreateModel(
            name='PetFood',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=50)),
                ('calories_serving', models.PositiveIntegerField(default=0)),
                ('cups_serving', models.FloatField(default=0)),
                ('density', models.FloatField(default=0)),
            ],
        ),
        migrations.CreateModel(
            name='Profile',
            fields=[
                ('user', models.OneToOneField(on_delete=django.db.models.deletion.CASCADE, primary_key=True, serialize=False, to=settings.AUTH_USER_MODEL)),
            ],
            options={
                'verbose_name': 'user',
                'verbose_name_plural': 'users',
                'abstract': False,
            },
            bases=('auth.user',),
            managers=[
                ('objects', django.contrib.auth.models.UserManager()),
            ],
        ),
        migrations.CreateModel(
            name='UserRequestAction',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('start_time', models.DateTimeField()),
                ('end_time', models.DateTimeField(blank=True, default=None)),
                ('request_type', models.IntegerField()),
                ('status', models.IntegerField()),
                ('feeder', models.ForeignKey(null=True, on_delete=django.db.models.deletion.SET_NULL, to='petfeeder.PetFeeder')),
                ('user', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to=settings.AUTH_USER_MODEL)),
            ],
        ),
        migrations.AddField(
            model_name='petfood',
            name='user',
            field=models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to=settings.AUTH_USER_MODEL),
        ),
        migrations.AddField(
            model_name='petfeeder',
            name='food',
            field=models.ForeignKey(null=True, on_delete=django.db.models.deletion.SET_NULL, to='petfeeder.PetFood'),
        ),
        migrations.AddField(
            model_name='petfeeder',
            name='pet',
            field=models.OneToOneField(null=True, on_delete=django.db.models.deletion.SET_NULL, to='petfeeder.Pet'),
        ),
        migrations.AddField(
            model_name='petfeeder',
            name='user',
            field=models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to=settings.AUTH_USER_MODEL),
        ),
        migrations.AddField(
            model_name='petconsumptionaction',
            name='food',
            field=models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='petfeeder.PetFood'),
        ),
        migrations.AddField(
            model_name='petconsumptionaction',
            name='pet',
            field=models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='petfeeder.Pet'),
        ),
        migrations.AddField(
            model_name='pet',
            name='user',
            field=models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to=settings.AUTH_USER_MODEL),
        ),
        migrations.AddField(
            model_name='fooddispenseraction',
            name='feeder',
            field=models.ForeignKey(null=True, on_delete=django.db.models.deletion.SET_NULL, to='petfeeder.PetFeeder'),
        ),
        migrations.AddField(
            model_name='fooddispenseraction',
            name='food',
            field=models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='petfeeder.PetFood'),
        ),
    ]

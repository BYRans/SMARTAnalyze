rm(list=ls())
gc()

memory.limit(4000)
library(corrplot)
library(rpart)
data_health<-read.csv("D:/smart_data/smart_data_toshiba_good_mbf2300r_5.txt",header=FALSE,sep="\t",na.strings="None")#读健康数据
data_fault<-read.csv("D:/smart_data/smart_data_toshiba_bad_mbf2300r_last24h_training.txt",header=FALSE,sep="\t",na.strings="None")#读故障数据-训练数据
data_fault_test<-read.csv("D:/smart_data/smart_data_toshiba_bad_mbf2300r_last24h_test.txt",header=FALSE,sep="\t",na.strings="None")#读故障数据—测试数据

colnames(data_health) <- c("id","serial_number","update_time","smart_health_status","current_drive_temperature","drive_trip_temperature","elements_in_grown_defect_list","manufactured_time","cycle_count","load_unload_count","load_unload_count","load_unload_cycles","blocks_sent_to_initiator","blocks_received_from_initiator","blocks_read_from_cache","num_commands_size_not_larger_than_segment_size ","num_commands_size_larger_than_segment_size","num_hours_powered_up","num_minutes_next_test","read_corrected_ecc_fast","read_corrected_ecc_delayed","read_corrected_re","read_total_errors_corrected","read_correction_algo_invocations","read_gigabytes_processed","read_total_uncorrected_errors","write_corrected_ecc_fast","write_corrected_ecc_delayed","write_corrected_re","write_total_errors_corrected","write_correction_algo_invocations","write_gigabytes_processed","write_total_uncorrected_errors","verify_corrected_ecc_fast","verify_corrected_ecc_delayed","verify_corrected_re","verify_total_errors_corrected","verify_correction_algo_invocations","verify_gigabytes_processed","verify_total_uncorrected_errors","non_medium_error_count")  #列改名

colnames(data_fault) <- c("id","serial_number","update_time","smart_health_status","current_drive_temperature","drive_trip_temperature","elements_in_grown_defect_list","manufactured_time","cycle_count","load_unload_count","load_unload_count","load_unload_cycles","blocks_sent_to_initiator","blocks_received_from_initiator","blocks_read_from_cache","num_commands_size_not_larger_than_segment_size ","num_commands_size_larger_than_segment_size","num_hours_powered_up","num_minutes_next_test","read_corrected_ecc_fast","read_corrected_ecc_delayed","read_corrected_re","read_total_errors_corrected","read_correction_algo_invocations","read_gigabytes_processed","read_total_uncorrected_errors","write_corrected_ecc_fast","write_corrected_ecc_delayed","write_corrected_re","write_total_errors_corrected","write_correction_algo_invocations","write_gigabytes_processed","write_total_uncorrected_errors","verify_corrected_ecc_fast","verify_corrected_ecc_delayed","verify_corrected_re","verify_total_errors_corrected","verify_correction_algo_invocations","verify_gigabytes_processed","verify_total_uncorrected_errors","non_medium_error_count")  #列改名

colnames(data_fault_test) <- c("id","serial_number","update_time","smart_health_status","current_drive_temperature","drive_trip_temperature","elements_in_grown_defect_list","manufactured_time","cycle_count","load_unload_count","load_unload_count","load_unload_cycles","blocks_sent_to_initiator","blocks_received_from_initiator","blocks_read_from_cache","num_commands_size_not_larger_than_segment_size ","num_commands_size_larger_than_segment_size","num_hours_powered_up","num_minutes_next_test","read_corrected_ecc_fast","read_corrected_ecc_delayed","read_corrected_re","read_total_errors_corrected","read_correction_algo_invocations","read_gigabytes_processed","read_total_uncorrected_errors","write_corrected_ecc_fast","write_corrected_ecc_delayed","write_corrected_re","write_total_errors_corrected","write_correction_algo_invocations","write_gigabytes_processed","write_total_uncorrected_errors","verify_corrected_ecc_fast","verify_corrected_ecc_delayed","verify_corrected_re","verify_total_errors_corrected","verify_correction_algo_invocations","verify_gigabytes_processed","verify_total_uncorrected_errors","non_medium_error_count")  #列改名

data_health$label <- 0
data_fault$label <- 1
data_fault_test$label <- 1

#决策树
n <- nrow(data_fault)
dataNewTraining<-rbind(data_fault,data_health[sample(1:(nrow(data_health[1:(nrow(data_health)*0.7),])),n*3),])
dataNewTest<-rbind(data_fault_test,data_health[-(1:(nrow(data_health)*0.7)),])

pdf(file='D:/smartDT.pdf',family="GB1")
dt <- rpart(label~ current_drive_temperature + drive_trip_temperature + elements_in_grown_defect_list + cycle_count + load_unload_count + load_unload_count + load_unload_cycles + read_corrected_ecc_fast + read_corrected_ecc_delayed + read_corrected_re + read_total_errors_corrected + read_correction_algo_invocations + read_gigabytes_processed + read_total_uncorrected_errors + write_corrected_ecc_fast + write_corrected_ecc_delayed + write_corrected_re + write_total_errors_corrected + write_correction_algo_invocations + write_gigabytes_processed + write_total_uncorrected_errors + non_medium_error_count ,data = dataNewTraining, method = "class")
plot(dt,main="smartDT");text(dt)
dev.off()

rawPredictScore = predict(dt,dataNewTest)
predictScore <- data.frame(rawPredictScore)
predictScore$label <- 2
predictScore[predictScore$X0 > predictScore$X1,][,"label"]=0
predictScore[predictScore$X0 <= predictScore$X1,][,"label"]=1

write.table(data.frame(predictScore$label,dataNewTest$label,dataNewTest$update_time,dataNewTest$serial_number), file="D:/smart_data/smartTestSetWithSerNO.txt",row.names= F ,col.names= F ,sep="\t") 



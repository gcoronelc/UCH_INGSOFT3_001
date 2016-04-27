package pe.egcc.model;

import java.util.ArrayList;
import java.util.List;
import pe.egcc.dto.ItemDto;

public class CarritoModel {

  private List<ItemDto> lista;

  public CarritoModel() {
    lista = new ArrayList<>();
  }

  public void agregarItem(ItemDto dto) {
    dto.setImporte(dto.getPrecio() * dto.getCant());
    lista.add(dto);
  }

  public List<ItemDto> getLista() {
    return lista;
  }
  
  public double getTotal(){
    double total = 0.0;
    for (ItemDto dto : lista) {
      total += dto.getImporte();
    }
    return total;
  }
  
//   public double getMp(){
//    double total = 0.0;
//    for (ItemDto dto : lista) {
//      total += dto.getImporte();
//    }
//    return total;
//  }
   public ItemDto getVentaMayorImporte(){
       if(lista.isEmpty()){
           return null;                  
       }
       ItemDto dto = lista.get(0);
       for(ItemDto obj : lista){
           if(obj.getImporte() > dto.getImporte()){
               dto = obj;
           }
       }
       return dto;
   }
      public ItemDto getVentaMenorImporte(){
       if(lista.isEmpty()){
           return null;                  
       }
       ItemDto dto = lista.get(0);
       for(ItemDto obj : lista){
           if(obj.getImporte() < dto.getImporte()){
               dto = obj;
           }
       }
       return dto;
   }
      
          public ItemDto getMenorVenta(){
       if(lista.isEmpty()){
           return null;                  
       }
       ItemDto dto = lista.get(0);
       for(ItemDto obj : lista){
           if(obj.getCant()< dto.getCant()){
               dto = obj;
           }
       }
       return dto;
   }
          
                    public ItemDto getMayorVenta(){
       if(lista.isEmpty()){
           return null;                  
       }
       ItemDto dto = lista.get(0);
       for(ItemDto obj : lista){
           if(obj.getCant()> dto.getCant()){
               dto = obj;
           }
       }
       return dto;
   }
      

//  public void mayorprecio(ItemDto dto){
//      
//      
////  }
//  public double getMayorPrecio(){
//      int contMp=0;
//      for(ItemDto dto : lista){
//          if("platano".equals(dto.getProducto())){
//              conMp += 
//          }
//      return contMp;        
//      }
//  }

}
